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

public final class ComparisonConnexityVerification<A> implements Verification<A> {
    private final CompareStrategy<A> strategy;

    public ComparisonConnexityVerification(final CompareStrategy<A> strategy) {
        this.strategy = strategy;
    }

    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances
    ) {
        // verify that sgn(a.compareTo(b)) == -sgn(b.compareTo(a))
        verifyReverse(equalInstances, equalInstances);
        verifyReverse(equalInstances, lesserInstances);
        verifyReverse(equalInstances, greaterInstances);
        verifyReverse(lesserInstances, greaterInstances);
    }

    // sgn(a.compareTo(b)) == -sgn(b.compareTo(a))
    // a.compareTo(b) should throw exception iff b.compareTo(a) throws
    private void verifyReverse(final List<A> first,
                               final List<A> second) {
        for (final A a : first) {
            for (final A b : second) {
                // getting info for sgn(a.compareTo(b)) part
                int signOfAtoB = Integer.MIN_VALUE;
                boolean exceptionOnAtoBCompare = false;
                try {
                    signOfAtoB = (int) Math.signum(strategy.compare(a, b));
                } catch (final Exception exc) {
                    exceptionOnAtoBCompare = true;
                }

                // getting info for sgn(b.compareTo(a)) part
                int signOfBtoA = Integer.MAX_VALUE;
                boolean exceptionOnBtoACompare = false;
                try {
                    signOfBtoA = (int) Math.signum(strategy.compare(b, a));
                } catch (final Exception exc) {
                    exceptionOnBtoACompare = true;
                }

                // if the a.compareTo(b) threw an exception but b.compareTo(a) did not
                if (exceptionOnAtoBCompare && !exceptionOnBtoACompare) {
                    throw new AssertionError(
                            format("Comparing %s to %s threw an exception but %s to %s did not!", a, b, b, a)
                    );
                }
                // if the b.compareTo(a) threw an exception but a.compareTo(b) did not
                if (!exceptionOnAtoBCompare && exceptionOnBtoACompare) {
                    throw new AssertionError(
                            format("Comparing %s to %s threw an exception but %s to %s did not!", b, a, a, b)
                    );
                }
                // if sgn(a.compareTo(b)) != -sgn(b.compareTo(a))
                if (signOfAtoB != -signOfBtoA) {
                    throw new AssertionError("Instances do not implement a total order!");
                }
            }
        }
    }
}
