package com.olegpetrov.transaction.reconciliation.domain;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public record ReconciliationResult(
        TransactionsListReconciliationResult firstTransactionsResult,
        TransactionsListReconciliationResult secondTransactionsResult,
        List<Pair<Transaction, Transaction>> notPerfectlyMatchedTransactions,
        List<Transaction> firstNotMatchedTransactions,
        List<Transaction> secondNotMatchedTransactions) {
}
