package com.olegpetrov.transaction.reconciliation.service;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.olegpetrov.transaction.reconciliation.domain.Transaction;
import com.opencsv.CSVReader;

import lombok.SneakyThrows;

@Service
public class FileParsingService {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public Pair<List<Transaction>, List<Transaction>> parseFiles(MultipartFile file1, MultipartFile file2) {
    return Pair.of(parseCsv(file1), parseCsv(file2));
  }

  @SneakyThrows
  public List<Transaction> parseCsv(MultipartFile file) {
    List<Transaction> transactions = new ArrayList<>();

    try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
      String[] values;
      // Skip header
      csvReader.readNext();

      while ((values = csvReader.readNext()) != null) {
        Transaction transaction = Transaction.builder()
                .profileName(values[0])
                .transactionDate(LocalDateTime.from(formatter.parse(values[1])))
                .transactionAmount(new BigDecimal(values[2]))
                .transactionNarrative(values[3])
                .transactionDescription(values[4])
                .transactionId(values[5])
                .transactionType(values[6])
                .walletReference(values[7])
                .build();

        transactions.add(transaction);
      }
    }
    return transactions;
  }
}
