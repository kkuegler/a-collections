package com.ajjpj.acollections.internal;

import com.ajjpj.acollections.ACollectionBuilder;
import com.ajjpj.acollections.AIterator;
import com.ajjpj.acollections.AList;
import com.ajjpj.acollections.AMap;
import com.ajjpj.acollections.util.AOption;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;


public interface AListDefaults<T, C extends AList<T>> extends ACollectionDefaults<T,C>, AList<T> {
    AList<T> updated(int idx, T o);
    default C patch(int idx, List<T> patch, int numReplaced) {
        if(idx<0) throw new IndexOutOfBoundsException();
        //noinspection unchecked
        final ACollectionBuilder<T, C> builder = (ACollectionBuilder<T, C>) newBuilder();

        final Iterator<T> it = iterator();
        for(int i=0; i<idx; i++) {
            if (! it.hasNext()) throw new IndexOutOfBoundsException();
            builder.add(it.next());
        }

        for (T el: patch)
            builder.add(el);
        for (int i=0; i<numReplaced; i++) {
            if (! it.hasNext()) throw new IndexOutOfBoundsException();
            it.next();
        }

        builder.addAll(it);
        return builder.build();
    }

    default AOption<T> lastOption() {
        if (isEmpty()) return AOption.none();
        else return AOption.some(last());
    }
    @Override default C tail() {
        if (isEmpty()) throw new NoSuchElementException();
        return drop(1);
    }
    @Override default T last() {
        if (isEmpty()) throw new NoSuchElementException();
        return get(size() - 1);
    }
    @Override default C withoutLast () {
        if (isEmpty()) throw new NoSuchElementException();
        return dropRight(1);
    }

    C take(int n);
    C takeRight(int n);
    default C takeWhile(Predicate<T> f) {
        //noinspection unchecked
        final ACollectionBuilder<T, C> builder = (ACollectionBuilder<T, C>) newBuilder();
        for (T o: this) {
            if (!f.test(o)) break;
            builder.add(o);
        }
        return builder.build();
    }

    C drop(int n);
    C dropRight(int n);
    default C dropWhile(Predicate<T> f) {
        //noinspection unchecked
        final ACollectionBuilder<T, C> builder = (ACollectionBuilder<T, C>) newBuilder();
        boolean go = false;
        for (T o: this) {
            if (!go && !f.test(o)) go = true;
            if (go) builder.add(o);
        }
        return builder.build();
    }

    default C slice(int from, int to) {
        final AIterator<T> it = iterator().drop(from);
        final ACollectionBuilder<T, ? extends AList<T>> builder = this.newBuilder();

        for (int i=Math.max(from, 0); i<to; i++) {
            if (!it.hasNext()) break;
            builder.add(it.next());
        }

        //noinspection unchecked
        return (C) builder.build();
    }

    default C reverse() {
        //noinspection unchecked
        return (C) this.<T>newBuilder()
                .addAll(reverseIterator())
                .build();
    }
    AIterator<T> reverseIterator();

    @Override default C sorted (Comparator<? super T> comparator) {
        //noinspection unchecked
        final T[] data = (T[]) toArray();
        Arrays.sort(data, comparator);
        //noinspection unchecked
        return (C) this.<T>newBuilder().addAll(data).build();

    }
    @Override default C sorted () {
        //noinspection unchecked
        return (C) sorted((Comparator) Comparator.naturalOrder());
    }
    @Override default <X extends Comparable<X>> C sortedBy (Function<T, X> keyExtractor) {
        return sorted(Comparator.comparing(keyExtractor));
    }

    @Override default C shuffled () {
        return shuffled(ThreadLocalRandom.current());
    }
    @Override default C shuffled (Random r) {
        final List<T> l = new ArrayList<T>(this);
        Collections.shuffle(l, r);
        //noinspection unchecked
        return (C) newBuilder().addAll(l).build();
    }

    default boolean contains(Object o) {
        return exists(el -> Objects.equals(el, o));
    }

    default boolean startsWith(List<T> that) {
        if (that.size() > this.size()) return false;

        final Iterator<T> itThis = this.iterator();
        for (T aThat: that) {
            if (! Objects.equals(itThis.next(), aThat))
                return false;
        }
        return true;
    }
    default boolean endsWith(List<T> that) {
        final Iterator<T> i = this.iterator().drop(size() - that.size());
        final Iterator<T> j = that.iterator();
        while (i.hasNext() && j.hasNext())
            if (! Objects.equals(i.next(), j.next()))
                return false;

        return ! j.hasNext();
    }

    default <U> U foldRight(U zero, BiFunction<U,T,U> f) {
        return reverseIterator().fold(zero, f);
    }
    default T reduceRight(BiFunction<T,T,T> f) {
        return reverseIterator().reduce(f);
    }
    default AOption<T> reduceRightOption(BiFunction<T,T,T> f) {
        if (isEmpty())
            return AOption.none();
        else
            return AOption.some(reduceRight(f));
    }

    @Override default ListIterator<T> listIterator(int index) {
        return new ASimpleListIterator<>(this.iterator(), index);
    }

    @Override default C filter(Predicate<T> f) {
        return ACollectionDefaults.super.filter(f);
    }
    @Override default C filterNot(Predicate<T> f) {
        return filter(f.negate());
    }

    @Override default <K> AMap<K, C> groupBy (Function<T, K> keyExtractor) {
        return ACollectionDefaults.super.groupBy(keyExtractor);
    }

    @Override default int indexOf (Object o) {
        int result = 0;
        for (T el: this) {
            if (Objects.equals(el, o)) return result;
            result += 1;
        }
        return -1;
    }

    @Override default int lastIndexOf (Object o) {
        int result = size()-1;
        final Iterator<T> it = reverseIterator();
        while (it.hasNext()) {
            if (Objects.equals(it.next(), o)) return result;
            result -= 1;
        }
        return -1;
    }

    default boolean containsAll (Collection<?> c) {
        for(Object o: c)
            if (! contains(o)) return false;
        return true;
    }
}
