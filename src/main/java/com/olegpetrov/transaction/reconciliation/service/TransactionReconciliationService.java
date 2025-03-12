package com.olegpetrov.transaction.reconciliation.service;

import static com.olegpetrov.transaction.reconciliation.utils.ListUtils.getLeftList;
import static com.olegpetrov.transaction.reconciliation.utils.ListUtils.getRightList;
import static com.olegpetrov.transaction.reconciliation.utils.ListUtils.toList;
import static org.apache.commons.collections4.CollectionUtils.disjunction;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import com.olegpetrov.transaction.reconciliation.domain.ReconciliationResult;
import com.olegpetrov.transaction.reconciliation.domain.Transaction;
import com.olegpetrov.transaction.reconciliation.domain.TransactionsListReconciliationResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionReconciliationService {

  private static final LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10000);

  public ReconciliationResult reconcileTransactions(List<Transaction> firstTransactions, List<Transaction> secondTransactions) {
    ReconciliationResult reconciliationResultForFirst = getReconciliationResult(firstTransactions, secondTransactions);
    ReconciliationResult reconciliationResultForSecond = getReconciliationResult(secondTransactions, firstTransactions);

    return new ReconciliationResult(reconciliationResultForFirst.firstTransactionsResult(),
            reconciliationResultForSecond.firstTransactionsResult(),
            reconciliationResultForFirst.notPerfectlyMatchedTransactions(),
            reconciliationResultForFirst.firstNotMatchedTransactions(),
            reconciliationResultForSecond.firstNotMatchedTransactions());
  }

  public ReconciliationResult getReconciliationResult(List<Transaction> firstTransactions, List<Transaction> secondTransactions) {
    List<Pair<Transaction, Transaction>> perfectlyMatchingTransactions = findPerfectlyMatchingTransactions(firstTransactions, secondTransactions);

    List<Transaction> firstTransactionsRemainedAfterPerfectMatch = toList(disjunction(firstTransactions, getLeftList(perfectlyMatchingTransactions)));
    List<Transaction> secondTransactionsRemainedAfterPerfectMatch = toList(disjunction(secondTransactions,
            getRightList(perfectlyMatchingTransactions)));

    List<Pair<Transaction, Transaction>> notPerfectlyMatchingTransactions = findNotPerfectlyMatchingTransactions(
            firstTransactionsRemainedAfterPerfectMatch,
            secondTransactionsRemainedAfterPerfectMatch);

    List<Transaction> firstUnmatchedTransactions = toList(disjunction(firstTransactionsRemainedAfterPerfectMatch,
            getLeftList(notPerfectlyMatchingTransactions)));
    List<Transaction> secondUnmatchedTransactions = toList(disjunction(secondTransactionsRemainedAfterPerfectMatch,
            getLeftList(notPerfectlyMatchingTransactions)));

    return new ReconciliationResult(new TransactionsListReconciliationResult(firstTransactions.size(),
            perfectlyMatchingTransactions.size(),
            notPerfectlyMatchingTransactions.size(),
            firstUnmatchedTransactions.size()),
            new TransactionsListReconciliationResult(secondTransactions.size(),
                    perfectlyMatchingTransactions.size(),
                    notPerfectlyMatchingTransactions.size(),
                    secondUnmatchedTransactions.size()),
            notPerfectlyMatchingTransactions,
            firstUnmatchedTransactions,
            secondUnmatchedTransactions);
  }

  private List<Pair<Transaction, Transaction>> findPerfectlyMatchingTransactions(List<Transaction> firstTransactions,
          List<Transaction> secondTransactions) {
    Map<Pair<String, String>, Transaction> secondTransactionsPerUniquenessKey = secondTransactions.stream()
            .collect(Collectors.toMap(transaction -> Pair.of(transaction.getTransactionId(), transaction.getTransactionDescription()),
                    Function.identity(),
                    (existing, replacement) -> existing));

    return firstTransactions.stream()
            .map(transaction -> Pair.of(transaction,
                    secondTransactionsPerUniquenessKey.get(Pair.of(transaction.getTransactionId(), transaction.getTransactionDescription()))))
            .filter(pair -> pair.getRight() != null)
            .toList();
  }

  private List<Pair<Transaction, Transaction>> findNotPerfectlyMatchingTransactions(List<Transaction> firstTransactions,
          List<Transaction> secondTransactions) {
    List<Pair<Transaction, Transaction>> notPerfectlyMatchingTransactions = new ArrayList<>();

    for (Transaction firstTransaction : firstTransactions) {
      for (Transaction secondTransaction : secondTransactions) {
        if (isNotPerfectMatch(firstTransaction, secondTransaction)) {
          notPerfectlyMatchingTransactions.add(Pair.of(firstTransaction, secondTransaction));
          break;
        }
      }
    }
    return notPerfectlyMatchingTransactions;
  }

  private boolean isNotPerfectMatch(Transaction firstTransaction, Transaction secondTransaction) {
    return firstTransaction.getTransactionAmount().equals(secondTransaction.getTransactionAmount()) &&
            firstTransaction.getWalletReference().equals(secondTransaction.getWalletReference()) &&
            firstTransaction.getTransactionDescription().equals(secondTransaction.getTransactionDescription()) &&
            Math.abs(Duration.between(firstTransaction.getTransactionDate(), secondTransaction.getTransactionDate()).toMillis()) < 1000 &&
            levenshteinDistance.apply(firstTransaction.getTransactionNarrative(), secondTransaction.getTransactionNarrative()) <= 5;
  }
}
