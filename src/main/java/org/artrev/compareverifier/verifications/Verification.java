package org.artrev.compareverifier.verifications;

import java.util.List;

/**
 * Defines a common interface for all verification strategies used by
 * the verifier.
 *
 * @param <A> type of the elements checked by this verification strategy
 */
public interface Verification<A> {
    /**
     * @param lesserInstances  each value on the list is lesser then values
     *                         passed as equal instances and greater instances.
     *                         This list contains at least one element.
     * @param equalInstances   each value on the list is equal to other values on
     *                         this list. Each value on the list is greater
     *                         then values passed as lesser instances but lower
     *                         then the values passed as greater instances. This
     *                         list contains at least two elements that are not
     *                         the same reference.
     * @param greaterInstances each value on the list is greater then values
     *                         passed both on lesser and equal lists. This
     *                         list contains at least one element.
     * @throws AssertionError if the instances passed to the Verification
     *                        strategy do not satisfy it.
     */
    void verify(
            List<A> lesserInstances,
            List<A> equalInstances,
            List<A> greaterInstances
    );
}
