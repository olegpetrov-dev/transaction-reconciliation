package com.olegpetrov.transaction.reconciliation.controller;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.olegpetrov.transaction.reconciliation.domain.ReconciliationResult;
import com.olegpetrov.transaction.reconciliation.domain.Transaction;
import com.olegpetrov.transaction.reconciliation.domain.TransactionsListReconciliationResult;
import com.olegpetrov.transaction.reconciliation.service.FileParsingService;
import com.olegpetrov.transaction.reconciliation.service.TransactionReconciliationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TransactionsController {

  private final TransactionReconciliationService transactionReconciliationService;

  private final FileParsingService fileParsingService;

  @GetMapping("/index")
  public String index() {
    return "index";
  }

  @PostMapping("/files")
  public String uploadFiles(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2, Model model) {

    Pair<List<Transaction>, List<Transaction>> transactions = fileParsingService.parseFiles(file1, file2);
    ReconciliationResult reconciliationResult = transactionReconciliationService.reconcileTransactions(transactions.getLeft(),
            transactions.getRight());
    TransactionsListReconciliationResult firstTransactionsResult = reconciliationResult.firstTransactionsResult();
    TransactionsListReconciliationResult secondTransactionsResult = reconciliationResult.secondTransactionsResult();



    model.addAttribute("file1Name", file1.getOriginalFilename());
    model.addAttribute("firstTransactionsTotalCount", firstTransactionsResult.totalCount());
    model.addAttribute("firstTransactionsMatchingCount", firstTransactionsResult.matchingCount());
    model.addAttribute("firstTransactionsNotPerfectlyMatchingCount", firstTransactionsResult.notPerfectlyMatchingCount());
    model.addAttribute("firstTransactionsNotMatchingCount", firstTransactionsResult.notMatchingCount());

    model.addAttribute("file2Name", file2.getOriginalFilename());
    model.addAttribute("secondTransactionsTotalCount", secondTransactionsResult.totalCount());
    model.addAttribute("secondTransactionsMatchingCount", secondTransactionsResult.matchingCount());
    model.addAttribute("secondTransactionsNotPerfectlyMatchingCount", secondTransactionsResult.notPerfectlyMatchingCount());
    model.addAttribute("secondTransactionsNotMatchingCount", secondTransactionsResult.notMatchingCount());

    model.addAttribute("notPerfectlyMatchedTransactions", reconciliationResult.notPerfectlyMatchedTransactions());
    model.addAttribute("firstNotMatchedTransactions", reconciliationResult.firstNotMatchedTransactions());
    model.addAttribute("secondNotMatchedTransactions", reconciliationResult.secondNotMatchedTransactions());

    return "result";
  }


}
