package com.olegpetrov.transaction.reconciliation.utils;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class ListUtils {

  public static <L, R> List<L> getLeftList(List<Pair<L, R>> list) {
    return list.stream().map(Pair::getLeft).toList();
  }

  public static <L, R> List<R> getRightList(List<Pair<L, R>> list) {
    return list.stream().map(Pair::getRight).toList();
  }

  public static <E> List<E> toList(Collection<E> collection) {
    return collection.stream().toList();
  }
}
