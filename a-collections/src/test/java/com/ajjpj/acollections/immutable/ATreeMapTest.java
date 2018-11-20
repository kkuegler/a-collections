package com.ajjpj.acollections.immutable;

import com.ajjpj.acollections.AMapTests;
import com.ajjpj.acollections.TestHelpers;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class ATreeMapTest implements AMapTests {
    @Override public Iterable<Variant> variants () {
        return Arrays.asList(
                new Variant(() -> ATreeMap.builder(Comparator.<Integer>naturalOrder()), AVector.of(1, 2, 3)),
                new Variant(() -> ATreeMap.builder(Comparator.<Integer>naturalOrder().reversed()), AVector.of(3, 2, 1))
        );
    }

    @Override @Test public void testSerDeser () {
        doTest(v -> {
            assertEquals(v.mkMap(), TestHelpers.serDeser(v.mkMap()));
            assertEquals(v.mkMap(1), TestHelpers.serDeser(v.mkMap(1)));
            assertEquals(v.mkMap(1, 2, 3), TestHelpers.serDeser(v.mkMap(1, 2, 3)));
        });
    }

    private final int size = 100_000;
    private final int numIter = 1_000_000;

    private Map<Integer, Integer> createJu() {
        final Random rand = new Random(12345);
        final Map<Integer, Integer> result = new TreeMap<>();

        for(int i=0; i<numIter; i++) {
            final int key = rand.nextInt(size);
            final boolean add = rand.nextBoolean();

            if(add)
                result.put(key, key);
            else
                result.remove(key);
        }
        return result;
    }
    private ATreeMap<Integer, Integer> createA() {
        final Random rand = new Random(12345);
        ATreeMap<Integer, Integer> result = ATreeMap.empty(Comparator.<Integer>naturalOrder());

        for(int i=0; i<numIter; i++) {
            final int key = rand.nextInt(size);
            final boolean add = rand.nextBoolean();

            if(add)
                result = result.plus(key, key);
            else
                result = result.minus(key);
        }
        return result;
    }

    @Test void testAddRemove() {
        final Map<Integer, Integer> juMap = createJu();
        final ATreeMap<Integer, Integer> aMap = createA();

        assertEquals(juMap.size(), aMap.size());

        for (int i=0; i<size; i++) {
            assertEquals(juMap.get(i), aMap.get(i));
        }

        // test iteration
        final HashMap<Integer,Integer> juMap2 = new HashMap<>();
        for (Map.Entry<Integer, Integer> o: aMap) {
            juMap2.put(o.getKey(), o.getValue());
        }
        assertEquals(juMap, juMap2);
    }

}
