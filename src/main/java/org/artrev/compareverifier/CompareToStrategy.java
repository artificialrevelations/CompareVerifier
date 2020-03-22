package org.artrev.compareverifier;

/**
 * @param <A>
 */
class CompareToStrategy<A extends Comparable<A>> implements CompareStrategy<A> {

    CompareToStrategy() {
    }

    @Override
    public int compareTo(final A first, final A second) {
        return first.compareTo(second);
    }
}