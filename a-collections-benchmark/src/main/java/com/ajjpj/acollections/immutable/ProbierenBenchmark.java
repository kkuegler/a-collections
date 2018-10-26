package com.ajjpj.acollections.immutable;

import com.ajjpj.acollections.immutable.rbs.RedBlackTree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import scala.math.LowPriorityOrderingImplicits;
import scala.math.Ordering;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Warmup(iterations = 1, time=10, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@Measurement(iterations = 3, time=5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class ProbierenBenchmark {
    private static final int size = 100_000;
    private static final int numIter = 10_000_000;


    @Benchmark
    public void testModifyAColl(Blackhole bh) {
        final Random rand = new Random(12345);
        final Comparator<Integer> ordering = Comparator.naturalOrder();
        RedBlackTree.Tree<Integer,Integer> root = null;

        for(int i=0; i<numIter; i++) {
            final int key = rand.nextInt(size);
            root = RedBlackTree.update(root, key, key, true, ordering);
        }
        bh.consume(root);
    }

    @Benchmark
    public void testModifyScala(Blackhole bh) {
        final Random rand = new Random(12345);
        final Ordering<Integer> ordering = new LowPriorityOrderingImplicits(){}.comparatorToOrdering(Comparator.<Integer>naturalOrder());

        scala.collection.immutable.RedBlackTree.Tree<Integer,Integer> root = null;

        for(int i=0; i<numIter; i++) {
            final int key = rand.nextInt(size);
            root = scala.collection.immutable.RedBlackTree.update(root, key, key, true, ordering);
        }
        bh.consume(root);
    }
}