package com.ajjpj.acollections.immutable;

import com.ajjpj.acollections.ACollectionBuilder;
import com.ajjpj.acollections.ASet;
import com.ajjpj.acollections.ASetTests;
import com.ajjpj.acollections.util.AEquality;

import java.util.Arrays;
import java.util.Comparator;


public class SortedKeySetTest implements ASetTests {
    private static class KeySetBuilder implements ACollectionBuilder<Integer, ASet<Integer>> {
        private ATreeMap<Integer, Integer> map;

        KeySetBuilder(Comparator<Integer> comparator) {
            this.map = ATreeMap.empty(comparator);
        }

        @Override public ACollectionBuilder<Integer, ASet<Integer>> add (Integer el) {
            map = map.updated(el, el);
            return this;
        }

        @Override public ASet<Integer> build () {
            return map.keySet();
        }

        @Override public AEquality equality () {
            return map.keyEquality();
        }
    }

    @Override public boolean isSorted () {
        return true;
    }

    @Override public Iterable<Variant> variants () {
        return Arrays.asList(
                new Variant(() -> new KeySetBuilder(Comparator.naturalOrder()), AVector.of(1, 2, 3), false),
                new Variant(() -> new KeySetBuilder(Comparator.<Integer>naturalOrder().reversed()), AVector.of(3, 2, 1), false)
        );
    }
}