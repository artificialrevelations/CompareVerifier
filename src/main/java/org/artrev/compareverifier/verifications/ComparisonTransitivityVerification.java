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

import java.util.List;

import static java.lang.String.format;

public class ComparisonTransitivityVerification<A> implements Verification<A> {
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

    // sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0
    private void verifyTransitivity(final List<A> lesser,
                                    final List<A> equal,
                                    final List<A> greater) {
        for (final A a : lesser) {
            for (final A b : equal) {
                for (final A c : greater) {
                    final int equal_lesser = (int) Math.signum(strategy.compare(b, a));
                    final int greater_equal = (int) Math.signum(strategy.compare(c, b));
                    final int greater_lesser = (int) Math.signum(strategy.compare(c, a));

                    final boolean isTransitive =
                            equal_lesser > 0
                                    && greater_equal > 0
                                    && greater_lesser > 0;

                    if (!isTransitive) {
                        throw new AssertionError(
                                format("Instances %s, %s, %s, are not transitive!", a, b, c)
                        );
                    }
                }
            }
        }
    }
}
