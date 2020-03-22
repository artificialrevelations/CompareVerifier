package org.artrev.compareverifier;

import java.util.Comparator;

/**
 * @param <A>
 */
class ComparatorStrategy<A> implements CompareStrategy<A> {
    private final Comparator<A> comparator;

    /**
     * @param comparator
     */
    ComparatorStrategy(final Comparator<A> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compareTo(final A first, final A second) {
        return comparator.compare(first, second);
    }
}