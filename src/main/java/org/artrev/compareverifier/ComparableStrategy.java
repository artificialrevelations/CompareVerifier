package org.artrev.compareverifier;

/**
 * @param <A>
 */
class ComparableStrategy<A extends Comparable<A>> implements CompareStrategy<A> {

    ComparableStrategy() {
    }

    @Override
    public int compareTo(final A first, final A second) {
        return first.compareTo(second);
    }
}