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

/**
 * <pre>
 * {@code
 * Caution should be exercised when using a comparator capable of imposing an ordering
 * inconsistent with equals to order a sorted set (or sorted map). Suppose a sorted set
 * (or sorted map) with an explicit comparator c is used with elements (or keys) drawn
 * from a set S. If the ordering imposed by c on S is inconsistent with equals,
 * the sorted set (or sorted map) will behave "strangely." In particular the sorted set
 * (or sorted map) will violate the general contract for set (or map), which is
 * defined in terms of equals.
 * }
 * </pre>
 *
 * @param <A>
 * @see Verification
 * @see CompareStrategy
 */
public final class ConsistencyWithEqualsVerification<A> implements Verification<A> {
    private final CompareStrategy<A> strategy;

    public ConsistencyWithEqualsVerification(final CompareStrategy<A> strategy) {
        this.strategy = strategy;
    }

    @Override
    public void verify(
            final List<A> __,
            final List<A> instances,
            final List<A> ___
    ) {
        // verify that the returned instances are consistent with equals
        // we only check the instances created by the Equal instances creator
        // as they are supposed to be the same in terms of equals implementation
        final A instance = instances.get(0);
        for (final A a : instances) {
            final boolean isEqual = instance.equals(a);
            final boolean isSame = strategy.compare(instance, a) == 0;
            if (isEqual != isSame)
                throw new AssertionError("Consistency with equals missing!");
        }
    }
}
