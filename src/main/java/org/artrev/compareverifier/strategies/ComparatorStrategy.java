package org.artrev.compareverifier.strategies;

import java.util.Comparator;

/**
 * {@link CompareStrategy} implementation that is using an instance of {@link Comparator}
 * to perform each comparison.
 *
 * @param <A> type of the values compared by a {@link Comparator}.
 */
public final class ComparatorStrategy<A> implements CompareStrategy<A> {
    private final Comparator<A> comparator;

    public ComparatorStrategy(final Comparator<A> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(final A first, final A second) {
        return comparator.compare(first, second);
    }
}