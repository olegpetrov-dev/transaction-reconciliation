package com.olegpetrov.transaction.reconciliation.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"transactionId", "transactionDescription"})
@Builder
public class Transaction {

  private String profileName;
  private LocalDateTime transactionDate;
  private BigDecimal transactionAmount;
  private String transactionNarrative;
  private String transactionDescription;
  private String transactionId;
  private String transactionType;
  private String walletReference;

}
