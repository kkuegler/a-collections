package com.ajjpj.acollections;

import com.ajjpj.acollections.immutable.AHashSet;
import com.ajjpj.acollections.immutable.ALinkedList;
import com.ajjpj.acollections.immutable.ATreeSet;
import com.ajjpj.acollections.immutable.AVector;
import com.ajjpj.acollections.mutable.AMutableListWrapper;
import com.ajjpj.acollections.util.AOption;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;


public interface AIterator<T> extends Iterator<T> {
    static <T> AIterator<T> wrap(Iterator<T> inner) {
        return new AIteratorWrapper<>(inner);
    }

    static <T> AIterator<T> empty() {
        //noinspection unchecked
        return (AIterator<T>) AbstractAIterator.empty;
    }
    static <T> AIterator<T> single(T o) {
        return new AbstractAIterator.Single<>(o);
    }

    default AVector<T> toVector() {
        return AVector.fromIterator(this);
    }
    default ALinkedList<T> toLinkedList() {
        return ALinkedList.fromIterator(this);
    }
    default AHashSet<T> toSet() {
        return AHashSet.fromIterator(this);
    }
    default ATreeSet<T> toSortedSet() {
        //noinspection unchecked
        return ATreeSet.fromIterator(this, (Comparator) Comparator.naturalOrder());
    }
    default AMutableListWrapper toMutableList() {
        return AMutableListWrapper.fromIterator(this);
    }

    default boolean corresponds(Iterator<T> that) {
        return corresponds(that, Objects::equals);
    }
    default <U> boolean corresponds(Iterator<U> that, BiPredicate<T,U> f) {
        while (this.hasNext() && that.hasNext()) {
            if (!f.test(this.next(), that.next())) return false;
        }
        return this.hasNext() == that.hasNext();

    }

    default <U> AIterator<U> map(Function<T,U> f) {
        final AIterator<T> inner = this;
        return new AbstractAIterator<U>() {
            @Override public boolean hasNext () {
                return inner.hasNext();
            }
            @Override public U next () {
                return f.apply(inner.next());
            }
        };
    }

    AIterator<T> filter(Predicate<T> f);
    default AIterator<T> filterNot(Predicate<T> f) {
        return filter(f.negate());
    }

    default <U> AIterator<U> collect(Predicate<T> filter, Function<T,U> f) {
        return filter(filter).map(f);
    }
    default <U> AOption<U> collectFirst(Predicate<T> filter, Function<T,U> f) {
        final AIterator<U> it = collect(filter, f);
        if (it.hasNext())
            return AOption.some(it.next());
        return AOption.none();
    }

    default AIterator<T> drop(int n) {
        for (int i=0; i<n; i++)
            next();
        return this;
    }

    default AOption<T> find(Predicate<T> f) {
        while(hasNext()) {
            final T o = next();
            if (f.test(o)) return AOption.some(o);
        }
        return AOption.none();
    }

    default boolean forall(Predicate<T> f) {
        while(hasNext()) {
            if (! f.test(next())) return false;
        }
        return true;
    }
    default boolean exists(Predicate<T> f) {
        while(hasNext()) {
            if (f.test(next())) return true;
        }
        return false;
    }
    default int count(Predicate<T> f) {
        int result = 0;
        while(hasNext()) {
            if (f.test(next()))
                result += 1;
        }
        return result;
    }
    default T reduce(BiFunction<T,T,T> f) {
        if (! hasNext()) throw new NoSuchElementException();

        final T first = next();
        if (! hasNext()) return first;

        final T second = next();
        T result = f.apply(first, second);

        while (hasNext()) {
            result = f.apply(result, next());
        }
        return result;
    }
    default AOption<T> reduceOption(BiFunction<T,T,T> f) {
        if (! hasNext()) return AOption.none();
        return AOption.some(reduce(f));
    }

    default <U> U fold(U zero, BiFunction<U,T,U> f) {
        U result = zero;
        while(hasNext()) {
            result = f.apply(result, next());
        }
        return result;
    }

    default T min() {
        final Comparator comparator = Comparator.naturalOrder();
        //noinspection unchecked
        return min((Comparator<T>) comparator);
    }
    default T min(Comparator<? super T> comparator) {
        return reduce((a, b) -> comparator.compare(a, b) < 0 ? a : b);
    }
    default T max() {
        final Comparator comparator = Comparator.naturalOrder();
        //noinspection unchecked
        return max((Comparator<T>) comparator);
    }
    default T max(Comparator<? super T> comparator) {
        return reduce((a, b) -> comparator.compare(a, b) > 0 ? a : b);
    }

    default String mkString(String infix) {
        return mkString("", infix, "");
    }
    default String mkString(String prefix, String infix, String suffix) {
        final StringBuilder sb = new StringBuilder(prefix);

        if (hasNext()) {
            sb.append(next());
            while (hasNext()) {
                sb.append(infix);
                sb.append(next());
            }
        }

        sb.append(suffix);
        return sb.toString();
    }

    default AIterator<T> concat(Iterator<? extends T> other) {
        //noinspection unchecked
        return AIterator.concat(this, other);
    }

    @SafeVarargs
    static <T> AIterator<T> concat(Iterator<? extends T>... inner) {
        return wrap(new AbstractAIterator.ConcatIterator<>(inner));
    }
}
