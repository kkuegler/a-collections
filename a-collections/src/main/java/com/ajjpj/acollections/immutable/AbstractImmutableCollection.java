package com.ajjpj.acollections.immutable;

import com.ajjpj.acollections.ACollection;
import com.ajjpj.acollections.internal.ACollectionSupport;

import java.util.*;


public abstract class AbstractImmutableCollection<T> implements ACollection<T> {

    @Override public int hashCode () {
        //TODO do we want to cache?
        // we can not safely cache the hash code, even for immutable collections, because there is no way to
        //  be sure that the elements are immutable too :-(
        int result = 1;
        for (T o: this)
            result = 31*result + (o==null ? 0 : o.hashCode());

        return result;
    }

    @Override public Object[] toArray () {
        return ACollectionSupport.toArray(this);
    }

    @Override public <T1> T1[] toArray (T1[] a) {
        return ACollectionSupport.toArray(this, a);
    }

    @Override public boolean add (T t) { throw new UnsupportedOperationException("no mutable operations for immutable collection"); }
    @Override public boolean remove (Object o) { throw new UnsupportedOperationException("no mutable operations for immutable collection"); }
    @Override public boolean addAll (Collection<? extends T> c) { throw new UnsupportedOperationException("no mutable operations for immutable collection"); }
    @Override public boolean removeAll (Collection<?> c) { throw new UnsupportedOperationException("no mutable operations for immutable collection"); }
    @Override public boolean retainAll (Collection<?> c) { throw new UnsupportedOperationException("no mutable operations for immutable collection"); }
    @Override public void clear () { throw new UnsupportedOperationException("no mutable operations for immutable collection"); }
}
