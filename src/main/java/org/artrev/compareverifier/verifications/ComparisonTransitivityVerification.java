/*
  Copyright (c) 2020-present, CompareVerifier Contributors.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
 */
package org.artrev.compareverifier.verifications;

import org.artrev.compareverifier.strategies.CompareStrategy;

import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;

/**
 * Checks if the relation over elements of type A is transitive for all specified
 * elements supplied for check. A transitive relation R on a the set A is transitive if
 * <pre>
 * {@code for all a, b, c belonging to A, if a R b and b R c, then a R c}
 * </pre>
 * For {@link Comparable} it will check if:
 * <pre>
 * {@code sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0}
 * </pre>
 * For {@link Comparator} it will check if:
 * <pre>
 * {@code sgn(compare(a, b)) > 0 && sgn(compare(b, c)) > 0 => sgn(compare(a, c)) > 0}
 * </pre>
 * It can work with custom comparison interfaces through {@link CompareStrategy}.
 *
 * @param <A> type of the elements that are verified.
 * @see CompareStrategy
 * @see Comparable
 * @see Comparator
 */
public final class ComparisonTransitivityVerification<A> implements Verification<A> {
    private final CompareStrategy<A> strategy;

    public ComparisonTransitivityVerification(final CompareStrategy<A> strategy) {
        this.strategy = strategy;
    }

    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances
    ) {
        // verify that sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0
        verifyTransitivity(lesserInstances, equalInstances, greaterInstances);
    }

    private void verifyTransitivity(final List<A> lesserInstances,
                                    final List<A> equalInstances,
                                    final List<A> greaterInstances) {
        for (final A lesser : lesserInstances) {
            for (final A equal : equalInstances) {
                for (final A greater : greaterInstances) {
                    final int equal_lesser = (int) Math.signum(strategy.compare(equal, lesser));
                    final int greater_equal = (int) Math.signum(strategy.compare(greater, equal));
                    final int greater_lesser = (int) Math.signum(strategy.compare(greater, lesser));

                    final boolean isTransitive =
                            equal_lesser > 0
                                    && greater_equal > 0
                                    && greater_lesser > 0;

                    if (!isTransitive) {
                        throw new AssertionError(
                                format("Instances %s, %s, %s, are not transitive!", lesser, equal, greater)
                        );
                    }
                }
            }
        }
    }
}
