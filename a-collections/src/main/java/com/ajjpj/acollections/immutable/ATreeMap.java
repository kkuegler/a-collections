package com.ajjpj.acollections.immutable;

import com.ajjpj.acollections.*;
import com.ajjpj.acollections.internal.ACollectionDefaults;
import com.ajjpj.acollections.internal.ACollectionSupport;
import com.ajjpj.acollections.internal.AMapSupport;
import com.ajjpj.acollections.util.AEquality;
import com.ajjpj.acollections.util.AOption;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


public class ATreeMap<K,V> implements ASortedMap<K,V>, ACollectionDefaults<Map.Entry<K,V>, ATreeMap<K,V>> {
    private final RedBlackTree.Tree<K,V> root;
    private final Comparator<K> comparator;

    public static <K,V> ATreeMap<K,V> empty(Comparator<K> comparator) {
        return new ATreeMap<>(null, comparator);
    }

    private ATreeMap (RedBlackTree.Tree<K,V> root, Comparator<K> comparator) {
        this.root = root;
        this.comparator = comparator;
    }

    public static <K extends Comparable<K>,V> ATreeMap<K,V> fromIterator(Iterator<Entry<K,V>> iterator) {
        return fromIterator(iterator, Comparator.naturalOrder());
    }
    public static <K,V> ATreeMap<K,V> fromIterator(Iterator<Entry<K,V>> iterator, Comparator<K> comparator) {
        return ATreeMap.<K,V> builder(comparator).addAll(iterator).build();
    }
    public static <K extends Comparable<K>,V> ATreeMap<K,V> fromIterable(Iterable<Entry<K,V>> iterable) {
        return fromIterable(iterable, Comparator.naturalOrder());
    }
    public static <K,V> ATreeMap<K,V> fromIterable(Iterable<Entry<K,V>> iterator, Comparator<K> comparator) {
        return ATreeMap.<K,V> builder(comparator).addAll(iterator).build();
    }

    public static <K extends Comparable<K>,V> Builder<K,V> builder() {
        return builder(Comparator.<K>naturalOrder());
    }
    public static <K,V> Builder<K,V> builder(Comparator<K> comparator) {
        return new Builder<>(comparator);
    }

    @Override public V get(Object key) {
        //noinspection unchecked
        return RedBlackTree.get(root, (K) key, comparator).orNull(); //TODO skip 'get'
    }
    @Override public ATreeMap<K,V> updated(K key, V value) {
        return new ATreeMap<>(RedBlackTree.update(root, key, value, true, comparator), comparator);
    }
    @Override public ATreeMap<K,V> removed(K key) {
        return new ATreeMap<>(RedBlackTree.delete(root, key, comparator), comparator);
    }
    @Override public AIterator<Entry<K,V>> iterator() {
        return RedBlackTree.iterator(root, AOption.none(), comparator);
    }

    @Override public int size() {
        return RedBlackTree.count(root);
    }

    @Override public AEquality keyEquality () {
        return AEquality.fromComparator(comparator);
    }

    @Override public boolean containsKey (Object key) {
        //noinspection unchecked
        return RedBlackTree.get(root, (K) key, comparator).nonEmpty(); //TODO skip 'get', use 'lookup' directly
    }

    @Override public boolean containsValue (V value, AEquality equality) {
        return RedBlackTree.valuesIterator(root, AOption.none(), comparator)
                .exists(v -> equality.equals(v, value));
    }

    @Override public AOption<V> getOptional (K key) {
        return RedBlackTree.get(root, key, comparator);
    }

    @Override public <U> ACollection<U> map (Function<Entry<K, V>, U> f) {
        return ACollectionSupport.map(AVector.builder(), this, f);
    }

    @Override public <U> ACollection<U> flatMap (Function<Entry<K, V>, Iterable<U>> f) {
        return ACollectionSupport.flatMap(AVector.builder(), this, f);
    }

    @Override public <U> ACollection<U> collect (Predicate<Entry<K, V>> filter, Function<Entry<K, V>, U> f) {
        return ACollectionSupport.collect(AVector.builder(), this, filter, f);
    }

    @Override public ATreeMap<K, V> filter (Predicate<Entry<K, V>> f) {
        return ATreeMap.<K,V>builder(comparator).addAll(iterator().filter(f)).build();
    }

    @Override public boolean isEmpty () {
        return root == null;
    }

    @Override public boolean containsValue (Object value) {
        return values().contains(value);
    }

    @Override  public V put (K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override public V remove (Object key) {
        throw new UnsupportedOperationException();
    }

    @Override public void putAll (Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override public void clear () {
        throw new UnsupportedOperationException();
    }

    @Override public ASortedSet<K> keySet () {
        return new AMapSupport.SortedKeySet<>(this);
    }

    @Override public ACollection<V> values () {
        return new AMapSupport.ValueCollection<>(this);
    }

    @Override public Set<Entry<K, V>> entrySet () { //TODO ASortedSet
        return new AMapSupport.EntrySet<>(this);
    }

    @Override public Comparator<K> comparator () {
        return comparator;
    }

    @Override public int countInRange (AOption<K> from, AOption<K> to) {
        return RedBlackTree.countInRange(root, from, to, comparator);
    }

    @Override public ATreeMap<K, V> range (AOption<K> from, AOption<K> until) {
        return new ATreeMap<>(RedBlackTree.rangeImpl(root, from, until, comparator), comparator);
    }

    @Override public ATreeMap<K, V> drop (int n) {
        return new ATreeMap<>(RedBlackTree.drop(root, n), comparator);
    }

    @Override public ATreeMap<K, V> take (int n) {
        return new ATreeMap<>(RedBlackTree.take(root, n), comparator);
    }

    @Override public ATreeMap<K, V> slice (int from, int until) {
        return new ATreeMap<>(RedBlackTree.slice(root, from, until), comparator);
    }

    @Override public AOption<Entry<K, V>> smallest () {
        if (root == null) return AOption.none();
        return AOption.some(RedBlackTree.smallest(root).entry());
    }

    @Override public AOption<Entry<K, V>> greatest () {
        if (root == null) return AOption.none();
        return AOption.some(RedBlackTree.greatest(root).entry());
    }

    @Override public AIterator<K> keysIterator () {
        return keysIterator(AOption.none());
    }
    @Override public AIterator<V> valuesIterator () {
        return valuesIterator(AOption.none());
    }

    @Override public AIterator<Entry<K, V>> iterator (AOption<K> start) {
        return RedBlackTree.iterator(root, start, comparator);
    }
    @Override public AIterator<K> keysIterator (AOption<K> start) {
        return RedBlackTree.keysIterator(root, start, comparator);
    }
    @Override public AIterator<V> valuesIterator (AOption<K> start) {
        return RedBlackTree.valuesIterator(root, start, comparator);
    }

    @Override public <U> ACollectionBuilder<U, ? extends ACollection<U>> newBuilder () {
        throw new UnsupportedOperationException("Implementing this well goes beyond the boundaries of Java's type system. Use static AHashMap.builder() instead.");
    }

    private static class Builder<K,V> implements ACollectionBuilder<Map.Entry<K,V>, ATreeMap<K,V>> {
        private ATreeMap<K,V> result;

        Builder (Comparator<K> comparator) {
            this.result = ATreeMap.empty(comparator);
        }

        public ACollectionBuilder<Entry<K, V>, ATreeMap<K, V>> add (K key, V value) {
            result = result.updated(key, value);
            return this;
        }

        @Override public ACollectionBuilder<Entry<K, V>, ATreeMap<K, V>> add (Entry<K, V> el) {
            result = result.updated(el.getKey(), el.getValue());
            return this;
        }

        @Override public ATreeMap<K, V> build () {
            return result;
        }
    }
}