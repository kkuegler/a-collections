package com.ajjpj.acollections.immutable;

import com.ajjpj.acollections.AIterator;
import com.ajjpj.acollections.AbstractAIterator;

import java.util.*;


/**
 * Implementation note: This class in particular is an optimization idea from
 *  the <a href="https://github.com/andrewoma/dexx">Dexx collections library</a>.
 */
class CompactHashMap<X extends CompactHashMap.EntryWithEquality> {
    interface EntryWithEquality {
        boolean hasEqualKey(EntryWithEquality other);
        int keyHash();
    }

    protected static final CompactHashMap EMPTY = new CompactHashMap();

    public AIterator<X> iterator() {
        return AIterator.empty();
    }

    public int size() {
        return 0;
    }
    public boolean isEmpty() {
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <K, V, X extends EntryWithEquality> CompactHashMap<X> empty() {
        return EMPTY;
    }

    protected X get0(X kv, int level) {
        return null;
    }

    protected CompactHashMap<X> updated0(X kv, int level) {
        return new HashMap1<>(kv);
    }

    protected CompactHashMap<X> removed0(X kv, int level) { // entry instead of key as an optimization
        return this;
    }

    // utility method to create a HashTrieMap from two leaf CompactHashMaps (HashMap1 or CompactHashMapCollision1) with non-colliding hash code)
    static <X extends EntryWithEquality> HashTrieMap<X> makeHashTrieMap(int hash0, CompactHashMap<X> elem0, int hash1, CompactHashMap<X> elem1, int level, int size) {
        int index0 = (hash0 >>> level) & 0x1f;
        int index1 = (hash1 >>> level) & 0x1f;
        if (index0 != index1) {
            int bitmap = (1 << index0) | (1 << index1);
            @SuppressWarnings("unchecked")
            Object[] elems = new Object[2];
            if (index0 < index1) {
                elems[0] = elem0;
                elems[1] = elem1;
            } else {
                elems[0] = elem1;
                elems[1] = elem0;
            }
            return new HashTrieMap<>(bitmap, elems, size);
        } else {
            @SuppressWarnings("unchecked")
            Object[] elems = new Object[1];
            int bitmap = (1 << index0);
            elems[0] = makeHashTrieMap(hash0, elem0, hash1, elem1, level + 5, size);
            return new HashTrieMap<>(bitmap, elems, size);
        }
    }

    static class HashMap1<X extends EntryWithEquality> extends CompactHashMap<X> {
        private final X kv;

        HashMap1(X kv) {
            this.kv = kv;
        }

        @Override public int size() {
            return 1;
        }
        @Override public boolean isEmpty() {
            return false;
        }

        @Override protected X get0(X kv, int level) {
            if (this.kv.hasEqualKey(kv))
                return this.kv;
            else
                return null;
        }

        @Override protected CompactHashMap<X> updated0(X kv, int level) {
            if (this.kv.hasEqualKey(kv)) {
                return new HashMap1<>(kv);
            } else {
                if (kv.keyHash() != this.kv.keyHash()) {
                    // they have different hashes, but may collide at this level - find a level at which they don't
                    return makeHashTrieMap(this.kv.keyHash(), this, kv.keyHash(), new HashMap1<>(kv), level, 2);
                } else {
                    // 32-bit hash collision (rare, but not impossible)
                    return new HashMapCollision1<>(kv.keyHash(), CompactListMap.<X>empty().updated(this.kv).updated(kv));
                }
            }
        }

        @Override protected CompactHashMap<X> removed0(X entry, int level) {
            if (this.kv.hasEqualKey(entry))
                return CompactHashMap.empty();
            else
                return this;
        }

        @Override public AIterator<X> iterator() {
            return AIterator.single(kv);
        }
    }

    static class HashMapCollision1<X extends EntryWithEquality> extends CompactHashMap<X> {
        private final int hash;
        private final CompactListMap<X> kvs;

        HashMapCollision1(int hash, CompactListMap<X> kvs) {
            this.hash = hash; //TODO look up hash?
            this.kvs = kvs;
        }

        @Override public int size() {
            return kvs.size();
        }
        @Override public boolean isEmpty () {
            return false;
        }

        @Override protected X get0(X kv, int level) {
            if (hash != kv.keyHash()) return null;
            return kvs.get(kv);
        }

        @Override protected CompactHashMap<X> updated0(X kv, int level) {
            final int hash = kv.keyHash();
            if (hash == this.hash) {
                return new HashMapCollision1<>(hash, kvs.updated(kv));
            } else {
                return makeHashTrieMap(this.hash, this, hash, new HashMap1<>(kv), level, size() + 1);
            }
        }

        @Override protected CompactHashMap<X> removed0(X entry, int level) {
            if (entry.keyHash() == this.hash) {
                final CompactListMap<X> m = kvs.removed(entry);
                if (m.isEmpty()) return CompactHashMap.empty();
                if (m.tail().isEmpty()) return new HashMap1<>(m.head());
                return new HashMapCollision1<>(hash, m);
            }
            else {
                return this;
            }
        }

        @Override public AIterator<X> iterator() {
            return new AbstractAIterator<X>() {
                CompactListMap<X> next = kvs;

                @Override public boolean hasNext () {
                    return next.nonEmpty();
                }

                @Override public X next () {
                    final X result = next.head();
                    next = next.tail();
                    return result;
                }
            };
        }
    }

    static abstract class CompactListMap<X extends EntryWithEquality> {
        static <X extends EntryWithEquality> CompactListMap<X> empty() {
            //noinspection unchecked
            return (CompactListMap<X>) EMPTY;
        }

        abstract X head();
        abstract CompactListMap<X> tail();
        abstract X get (X key);
        abstract int size();
        boolean isEmpty() {
            return ! nonEmpty();
        }
        abstract boolean nonEmpty(); // is way more efficient than size()
        abstract CompactListMap<X> updated(X entry);
        abstract CompactListMap<X> removed(X entry); // only key is used - 'entry' is used as an optimization

        private static final CompactListMap EMPTY = new CompactListMap() {
            @Override
            EntryWithEquality head () {
                throw new UnsupportedOperationException();
            }

            @Override
            CompactListMap<?> tail () {
                throw new UnsupportedOperationException();
            }
            @Override
            EntryWithEquality get (EntryWithEquality key) {
                return null;
            }
            @Override int size () {
                return 0;
            }
            @Override boolean nonEmpty () {
                return false;
            }

            @Override CompactListMap updated (EntryWithEquality entry) {
                //noinspection unchecked
                return new CompactListMap.Node(entry, this);
            }
            @Override CompactListMap removed (EntryWithEquality entry) {
                return this;
            }
        };

        private static class Node<X extends EntryWithEquality> extends CompactListMap<X> {
            private final X entry;
            private final CompactListMap<X> tail;

            Node (X entry, CompactListMap<X> tail) {
                this.entry = entry;
                this.tail = tail;
            }

            @Override X head () {
                return entry;
            }

            @Override CompactListMap<X> tail () {
                return tail;
            }
            @Override X get (X kv) {
                CompactListMap<X> m = this;
                while(m.nonEmpty()) {
                    if(m.head().hasEqualKey(kv)) {
                        return m.head();
                    }
                    m = m.tail();
                }
                return null;
            }
            @Override int size() {
                int result = 1;
                CompactListMap<X> m = this;
                while(m.tail().nonEmpty()) {
                    m = m.tail();
                    result += 1;
                }
                return result;
            }

            @Override boolean nonEmpty () {
                return true;
            }

            @Override CompactListMap<X> updated (X entry) {
                return new CompactListMap.Node<>(entry, removed(entry));
            }
            @Override CompactListMap<X> removed (X entry) {
                int idx = 0;
                boolean hasMatch = false;

                CompactListMap<X> remaining = this;
                while(remaining.nonEmpty()) {
                    if (entry.hasEqualKey(remaining.head())) {
                        remaining = remaining.tail();
                        hasMatch = true;
                        break;
                    }
                    idx += 1;
                    remaining = remaining.tail();
                }
                if (! hasMatch) return this;

                CompactListMap<X> result = remaining;
                CompactListMap<X> iter = this;
                for (int i=0; i<idx; i++) {
                    result = new CompactListMap.Node<>(iter.head(), result);
                    iter = iter.tail ();
                }

                return result;
            }
        }
    }


    static class HashTrieMap<X extends EntryWithEquality> extends CompactHashMap<X> {
        private final int bitmap;
        private final Object[] elems;
        private final int size;

        HashTrieMap(int bitmap, Object[] elems, int size) {
            this.bitmap = bitmap;
            this.elems = elems;
            this.size = size;
        }

        @Override public int size() {
            return size;
        }
        @Override public boolean isEmpty () {
            return false;
        }

        Object[] getElems() {
            return elems;
        }

        private CompactHashMap<X> getElem(int index) {
            //noinspection unchecked
            return (CompactHashMap<X>) elems[index];
        }

        @Override protected X get0(X kv, int level) {
            final int index = (kv.keyHash() >>> level) & 0x1f;
            final int mask = (1 << index);
            if (bitmap == -1) {
                return getElem(index & 0x1f).get0(kv, level + 5);
            }
            else if ((bitmap & mask) != 0) {
                final int offset = Integer.bitCount(bitmap & (mask - 1));
                return getElem(offset).get0(kv, level + 5);
            }
            else {
                return null;
            }
        }

        @Override protected CompactHashMap<X> updated0(X kv, int level) {
            final int index = (kv.keyHash() >>> level) & 0x1f;
            final int mask = (1 << index);
            final int offset = Integer.bitCount(bitmap & (mask - 1));
            if ((bitmap & mask) != 0) {
                final CompactHashMap<X> sub = getElem(offset);
                final CompactHashMap<X> subNew = sub.updated0(kv, level + 5);
                if (subNew.equals(sub)) {
                    return this;
                }
                else {
                    @SuppressWarnings("unchecked")
                    final Object[] elemsNew = new Object[elems.length];
                    System.arraycopy(elems, 0, elemsNew, 0, elems.length);
                    elemsNew[offset] = subNew;
                    return new HashTrieMap<>(bitmap, elemsNew, size + (subNew.size() - sub.size()));
                }
            }
            else {
                @SuppressWarnings("unchecked")
                final Object[] elemsNew = new Object[elems.length + 1];
                System.arraycopy(elems, 0, elemsNew, 0, offset);
                elemsNew[offset] = new HashMap1<>(kv);
                System.arraycopy(elems, offset, elemsNew, offset + 1, elems.length - offset);
                return new HashTrieMap<>(bitmap | mask, elemsNew, size + 1);
            }
        }

        @Override protected CompactHashMap<X> removed0(X kv, int level) {
            final int index = (kv.keyHash() >>> level) & 0x1f;
            final int mask = (1 << index);
            final int offset = Integer.bitCount(bitmap & (mask - 1));
            if ((bitmap & mask) != 0) {
                final CompactHashMap<X> sub = getElem(offset);
                final CompactHashMap<X> subNew = sub.removed0(kv, level + 5);
                if (subNew.equals(sub)) {
                    return this;
                }
                else if (subNew.size() == 0) {
                    int bitmapNew = bitmap ^ mask;
                    if (bitmapNew != 0) {
                        @SuppressWarnings("unchecked")
                        final Object[] elemsNew = new Object[elems.length - 1];
                        System.arraycopy(elems, 0, elemsNew, 0, offset);
                        System.arraycopy(elems, offset + 1, elemsNew, offset, elems.length - offset - 1);
                        final int sizeNew = size - sub.size();
                        if (elemsNew.length == 1 && !(elemsNew[0] instanceof HashTrieMap)) {
                            //noinspection unchecked
                            return (CompactHashMap<X>) elemsNew[0];
                        }
                        else {
                            return new HashTrieMap<>(bitmapNew, elemsNew, sizeNew);
                        }
                    } else {
                        return CompactHashMap.empty();
                    }
                }
                else if (elems.length == 1 && !(subNew instanceof HashTrieMap)) {
                    return subNew;
                }
                else {
                    @SuppressWarnings("unchecked")
                    final Object[] elemsNew = new Object[elems.length];
                    System.arraycopy(elems, 0, elemsNew, 0, elems.length);
                    elemsNew[offset] = subNew;
                    final int sizeNew = size + (subNew.size() - sub.size());
                    return new HashTrieMap<>(bitmap, elemsNew, sizeNew);
                }
            } else {
                return this;
            }
        }

        @Override public AIterator<X> iterator() {
            return new Itr<>(elems);
        }
    }

    static class Itr<X extends EntryWithEquality> extends AbstractAIterator<X> {
        private final ArrayDeque<Snapshot> stack = new ArrayDeque<>();

        private Iterator<X> subIterator;
        private Snapshot current;
        private X next;

        Itr (Object[] elems) {
            current = new Snapshot(elems, 0);
            gotoNext();
        }

        private void gotoNext() {
            next = null;

            if (subIterator != null) {
                if (subIterator.hasNext()) {
                    next = subIterator.next();
                    return;
                } else {
                    subIterator = null;
                }
            }

            while (next == null) {
                if (current.pos == current.objects.length) {
                    // Exhausted current array, try the stack
                    if (stack.isEmpty()) {
                        return;
                    }
                    else {
                        current = stack.pop();
                    }
                }
                else {
                    Object object = current.objects[current.pos++];
                    if (object instanceof HashTrieMap) {
                        stack.push(current);
                        current = new Snapshot(((HashTrieMap) object).getElems(), 0);
                    }
                    else if (object instanceof HashMapCollision1) {
                        //noinspection unchecked
                        subIterator = ((HashMapCollision1) object).iterator();
                        next = subIterator.next();
                    }
                    else {
                        //noinspection unchecked
                        next = (X) ((HashMap1)object).kv;
                    }
                }
            }
        }

        @Override public boolean hasNext() {
            return next != null;
        }

        @Override public X next() {
            if (next == null) {
                throw new NoSuchElementException();
            }

            X result = next;
            gotoNext();
            return result;
        }

        @Override public void remove() {
            throw new UnsupportedOperationException();
        }

        static class Snapshot {
            final Object[] objects;
            int pos;

            Snapshot(Object[] objects, int pos) {
                this.objects = objects;
                this.pos = pos;
            }
        }
    }
}
