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

public final class ComparisonWithNullVerification<A> implements Verification<A> {
    private final CompareStrategy<A> strategy;

    public ComparisonWithNullVerification(final CompareStrategy<A> strategy) {
        this.strategy = strategy;
    }

    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances
    ) {
        verifyExceptionOnComparingWithNull(lesserInstances, "lesser");
        verifyExceptionOnComparingWithNull(lesserInstances, "equal");
        verifyExceptionOnComparingWithNull(lesserInstances, "greater");
    }

    @SuppressWarnings({"ConstantConditions"})
    private void verifyExceptionOnComparingWithNull(
            final List<A> instances,
            final String instancesType
    ) {
        for (final A instance : instances) {
            boolean contractIsBroken;
            try {
                strategy.compare(instance, null);
                contractIsBroken = true;
            } catch (final NullPointerException exc) {
                // this should throw an exception
                continue;
            }

            if (contractIsBroken)
                throw new AssertionError(
                        String.format(
                                "Provided %s instance: %s compared to null should throw an Exception!",
                                instancesType,
                                instance
                        )
                );
        }
    }
}
