package org.artrev.compareverifier;

import java.util.Comparator;

/**
 * @param <A>
 */
interface CompareStrategy<A> {
    /**
     * @param first
     * @param second
     * @return
     */
    int compareTo(A first, A second);
}
