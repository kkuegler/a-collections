package com.ajjpj.acollections;

import com.ajjpj.acollections.immutable.ATreeSet;
import com.ajjpj.acollections.util.AOption;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;


/**
 * This interface represents a {@link java.util.SortedSet} with additional API (mostly inherited from {@link ASet}). Every ASortedSet
 *  has in inherent {@link Comparator} defining an ordering (and equality); this is exposed via {@link #comparator()}. ASortedSet has some
 *  methods in addition to {@link ASet} which are based on the ordering defined by the comparator.
 *
 * @param <T> the set's element type
 */
public interface ASortedSet<T> extends ASet<T> {

    /**
     * Creates an empty {@link ATreeSet} with {@link Comparator#naturalOrder()}.
     * <p> This can later be modified by calling {@link #plus(Object)} or {@link #minus(Object)}. For creating a set with known elements,
     *  calling one of the {@code of} factory methods is usually more concise.
     *
     * @param <T> the new set's element type
     * @return an empty {@link ATreeSet}
     */
    static <T extends Comparable<T>> ATreeSet<T> empty() {
        return ATreeSet.empty();
    }

    /**
     * Creates an empty {@link ATreeSet} with a given {@link Comparator}.
     * <p> This can later be modified by calling {@link #plus(Object)} or {@link #minus(Object)}. For creating a set with known elements,
     *  calling one of the {@code of} factory methods is usually more concise.
     *
     * @param comparator the new set's comparator
     * @param <T> the new set's element type
     * @return an empty {@link ATreeSet}
     */
    static <T> ATreeSet<T> empty(Comparator<T> comparator) {
        return ATreeSet.empty(comparator);
    }

    /**
     * This is an alias for {@link #empty()} for consistency with Java 9 conventions - it creates an empty {@link ATreeSet} with
     *  {@link Comparator#naturalOrder()}.
     *
     * @param <T> the new set's element type
     * @return an empty {@link ATreeSet}
     */
    static <T extends Comparable<T>> ATreeSet<T> of() {
        return ATreeSet.of();
    }

    /**
     * Convenience factory method creating an {@link ATreeSet} with exactly one element and {@link Comparator#naturalOrder()}.
     *
     * @param o the single element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param <T> the new set's element type (can often be inferred from the parameter by the compiler)
     * @return the new {@link ATreeSet}
     */
    static <T extends Comparable<T>> ATreeSet<T> of(T o) {
        return ATreeSet.of(o);
    }

    /**
     * Convenience factory method creating an {@link ATreeSet} with exactly two elements and {@link Comparator#naturalOrder()}.
     *
     * @param o1 the first element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o2 the second element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param <T> the new set's element type (can often be inferred from the parameter by the compiler)
     * @return the new {@link ATreeSet}
     */
    static <T extends Comparable<T>> ATreeSet<T> of(T o1, T o2) {
        return ATreeSet.of(o1, o2);
    }

    /**
     * Convenience factory method creating an {@link ATreeSet} with exactly three elements and {@link Comparator#naturalOrder()}.
     *
     * @param o1 the first element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o2 the second element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o3 the third element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param <T> the new set's element type (can often be inferred from the parameter by the compiler)
     * @return the new {@link ATreeSet}
     */
    static <T extends Comparable<T>> ATreeSet<T> of(T o1, T o2, T o3) {
        return ATreeSet.of(o1, o2, o3);
    }

    /**
     * Convenience factory method creating an {@link ATreeSet} with exactly four elements and {@link Comparator#naturalOrder()}.
     *
     * @param o1 the first element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o2 the second element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o3 the third element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o4 the fourth element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param <T> the new set's element type (can often be inferred from the parameter by the compiler)
     * @return the new {@link ATreeSet}
     */
    static <T extends Comparable<T>> ATreeSet<T> of(T o1, T o2, T o3, T o4) {
        return ATreeSet.of(o1, o2, o3, o4);
    }

    /**
     * Convenience factory method creating an {@link ATreeSet} with more than four elements and {@link Comparator#naturalOrder()}.
     *
     * @param o1 the first element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o2 the second element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o3 the third element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o4 the fourth element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param o5 the fifth element for the new set. It must implement {@link Comparable} to work with {@link Comparator#naturalOrder()}.
     * @param others the (variable number of) additional elements
     * @param <T> the new set's element type (can often be inferred from the parameter by the compiler)
     * @return the new {@link ATreeSet}
     */
    @SafeVarargs static <T extends Comparable<T>> ATreeSet<T> of(T o1, T o2, T o3, T o4, T o5, T... others) {
        return ATreeSet.of(o1, o2, o3, o4, o5, others);
    }

    /**
     * Creates a new {@link ATreeSet} based on an array's elements.
     *
     * @param that the array from which the new set is initialized
     * @param <T> the set's element type
     * @return the new set
     */
    static <T extends Comparable<T>> ATreeSet<T> from(T[] that) {
        return ATreeSet.from(that);
    }

    /**
     * Creates a new {@link ATreeSet} based on an array's elements with a given {@link Comparator}.
     *
     * @param that the array from which the new set is initialized
     * @param comparator the new set's comparator
     * @param <T> the set's element type
     * @return the new set
     */
    static <T> ATreeSet<T> from(T[] that, Comparator<T> comparator) {
        return ATreeSet.from(that, comparator);
    }

    /**
     * Creates a new {@link ATreeSet} based on an {@link Iterable}'s elements.
     *
     * @param that the {@link Iterable} from which the new set is initialized
     * @param <T> the set's element type
     * @return the new set
     */
    static <T extends Comparable<T>> ATreeSet<T> from(Iterable<T> that) {
        return ATreeSet.from(that);
    }

    /**
     * Creates a new {@link ATreeSet} based on an {@link Iterable}'s elements with a given {@link Comparator}.
     *
     * @param that the {@link Iterable} from which the new set is initialized
     * @param comparator the new set's comparator
     * @param <T> the set's element type
     * @return the new set
     */
    static <T> ATreeSet<T> from(Iterable<T> that, Comparator<T> comparator) {
        return ATreeSet.from(that, comparator);
    }

    /**
     * Creates a new {@link ATreeSet} based on an {@link Iterator}'s elements.
     *
     * @param it the {@link Iterator} from which the new set is initialized
     * @param <T> the set's element type
     * @return the new set
     */
    static <T extends Comparable<T>> ATreeSet<T> fromIterator(Iterator<T> it) {
        return ATreeSet.fromIterator(it);
    }

    /**
     * Creates a new {@link ATreeSet} based on an {@link Iterator}'s elements with a given {@link Comparator}.
     *
     * @param it the {@link Iterator} from which the new set is initialized
     * @param comparator the new set's comparator
     * @param <T> the set's element type
     * @return the new set
     */
    static <T> ATreeSet<T> fromIterator(Iterator<T> it, Comparator<T> comparator) {
        return ATreeSet.fromIterator(it, comparator);
    }


    /**
     * Returns the comparator defining this set's ordering and equality.
     *
     * @return this set's comparator
     */
    Comparator<T> comparator();

    ASortedSet<T> plus (T o);
    ASortedSet<T> minus (T o);

    ASortedSet<T> union (Iterable<? extends T> that);
    ASortedSet<T> intersect (Set<T> that);
    ASortedSet<T> diff (Set<T> that);

    /**
     * Counts all the elements greater than or equal to a lower bound and less than an upper bound. Both bounds are optional.
     *
     * @param from the optional lower bound
     * @param to   the optional upper bound
     * @return the number of elements in the given range
     */
    int countInRange (AOption<T> from, AOption<T> to);

    /**
     * Returns a subset containing the elements greater than or equal to a lower bound and less than an upper bound. Both bounds are optional.
     * TODO exception, out of bounds, ...
     *
     * <p> For mutable collections, this operation modifies the collection, for immutable collections it returns a modified copy.
     *
     * @param from  the optional lower bound
     * @param until the optional upper bound
     * @return the subset of elements between the two bounds
     */
    ASortedSet<T> range (AOption<T> from, AOption<T> until);

    /**
     * Returns this set without the {@code n} smallest elements.
     * TODO exception, out of bounds, ...
     *
     * <p> For mutable collections, this operation modifies the collection, for immutable collections it returns a modified copy.
     *
     * @param n the number of smallest elements to drop
     * @return this set without the {@code n} smallest elements
     */
    ASortedSet<T> drop (int n);

    /**
     * Returns a subset of this set containing the {@code n} smallest elements.
     * TODO exception, out of bounds, ...
     *
     * <p> For mutable collections, this operation modifies the collection, for immutable collections it returns a modified copy.
     *
     * @param n the number of smallest elements to take
     * @return a subset containing the {@code n} smallest elements.
     */
    ASortedSet<T> take (int n);

    /**
     * Returns a subset of this set, dropping the first {@code from} elements and then taking the next {@code until - from} elements.
     *
     * TODO exception, out of bounds, ...
     *
     * <p> For mutable collections, this operation modifies the collection, for immutable collections it returns a modified copy.
     *
     * @param from  the first index to take
     * @param until the first index not to take
     * @return a subset of this set with elements in the index from from {@code from} until {@code until}.
     */
    ASortedSet<T> slice (int from, int until);

    /**
     * Returns this set's smallest element or {@link AOption#none()} if this set is empty.
     *
     * @return this set's smallest element
     */
    AOption<T> smallest();

    /**
     * Returns this set's greatest element or {@link AOption#none()} if this set is empty.
     *
     * @return this set's greatest element
     */
    AOption<T> greatest();

    /**
     * Returns an {@link AIterator} starting at a lower bound. More precisely, the returned iterator starts at the smallest element
     *  that is greater or equal to the lower bound. The lower bound is optional.
     *
     * //TODO optional upper bound?
     *
     * @param start the lower bound for the iterator
     * @return an iterator starting at a given lower bound
     */
    AIterator<T> iterator(AOption<T> start);

    AIterator<? extends ASortedSet<T>> subsets ();
    AIterator<? extends ASortedSet<T>> subsets (int len);
}
