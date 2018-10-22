package com.ajjpj.acollections.internal;

import com.ajjpj.acollections.ACollectionBuilder;
import com.ajjpj.acollections.AIterator;
import com.ajjpj.acollections.ASet;


public interface ASetDefaults<T, C extends ASet<T>> extends ASet<T> {
    //TODO other ASet methods


    @Override default AIterator<C> subsets () {
        //noinspection unchecked
        return ASetSupport.subsets(this, () -> ((ACollectionBuilder) newBuilder()));
    }

    @Override default AIterator<C> subsets (int len) {
        //noinspection unchecked
        return ASetSupport.subsets(len, this, () -> ((ACollectionBuilder) newBuilder()));
    }
}
