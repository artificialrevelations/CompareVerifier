package org.artrev.compareverifier.strategies;

/**
 *
 *
 * @param <A>
 */
public interface CompareStrategy<A> {
    /**
     * @param first
     * @param second
     * @return
     */
    int compareTo(A first, A second);
}
