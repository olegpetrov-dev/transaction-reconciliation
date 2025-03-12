package com.olegpetrov.transaction.reconciliation.domain;

public record TransactionsListReconciliationResult(
        int totalCount,
        int matchingCount,
        int notPerfectlyMatchingCount,
        int notMatchingCount) {

}
