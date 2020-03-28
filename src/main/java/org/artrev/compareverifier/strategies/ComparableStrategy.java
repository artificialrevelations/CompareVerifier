package org.artrev.compareverifier.strategies;

/**
 *
 *
 * @param <A> type of the values that
 */
public final class ComparableStrategy<A extends Comparable<A>> implements CompareStrategy<A> {
    ComparableStrategy() {}

    @Override
    public int compareTo(final A first, final A second) {
        return first.compareTo(second);
    }
}