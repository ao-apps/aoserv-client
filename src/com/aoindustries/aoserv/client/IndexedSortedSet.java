package com.aoindustries.aoserv.client;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;

/**
 * @author  AO Industries, Inc.
 *
 * @see  AOServService
 */
public class IndexedSortedSet<E> extends IndexedSet<E> implements SortedSet<E> {

    private static final long serialVersionUID = 1L;

    public static final IndexedSortedSet EMPTY_INDEXED_SORTED_SET = new IndexedSortedSet();

    @SuppressWarnings("unchecked")
    public static final <T> IndexedSortedSet<T> emptyIndexedSortedSet() {
        return (IndexedSortedSet<T>) EMPTY_INDEXED_SORTED_SET;
    }

    @SuppressWarnings("unchecked")
    public IndexedSortedSet() {
        super(Collections.EMPTY_SORTED_SET);
    }

    public IndexedSortedSet(E value) {
        super(Collections.singletonSortedSet(value));
    }

    /**
     * The wrapped set should not change underneath this set.
     */
    @SuppressWarnings("unchecked")
    public IndexedSortedSet(SortedSet<E> wrapped) {
        super(wrapped!=null && wrapped.isEmpty() ? Collections.EMPTY_SORTED_SET : wrapped);
    }

    public Comparator<? super E> comparator() {
        return ((SortedSet<E>)wrapped).comparator();
    }

    public SortedSet<E> subSet(E fromElement, E toElement) {
        SortedSet<E> subset = ((SortedSet<E>)wrapped).subSet(fromElement, toElement);
        int size = subset.size();
        if(size==0) return emptyIndexedSortedSet();
        if(size==1) return Collections.singletonSortedSet(subset.first());
        return new IndexedSortedSet<E>(subset);
    }

    public SortedSet<E> headSet(E toElement) {
        SortedSet<E> headset = ((SortedSet<E>)wrapped).headSet(toElement);
        int size = headset.size();
        if(size==0) return emptyIndexedSortedSet();
        if(size==1) return Collections.singletonSortedSet(headset.first());
        return new IndexedSortedSet<E>(headset);
    }

    public SortedSet<E> tailSet(E fromElement) {
        SortedSet<E> tailset = ((SortedSet<E>)wrapped).tailSet(fromElement);
        int size = tailset.size();
        if(size==0) return emptyIndexedSortedSet();
        if(size==1) return Collections.singletonSortedSet(tailset.first());
        return new IndexedSortedSet<E>(tailset);
    }

    public E first() {
        return ((SortedSet<E>)wrapped).first();
    }

    public E last() {
        return ((SortedSet<E>)wrapped).last();
    }

    /**
     * Use the singleton for any empty set.
     */
    private Object readResolve() {
        return wrapped.isEmpty() ? EMPTY_INDEXED_SET : this;
    }
}
