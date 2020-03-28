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
package org.artrev.compareverifier.strategies;

import java.util.Comparator;

/**
 * {@link CompareStrategy} implementation that is using an instance of {@link Comparator}
 * to perform each comparison.
 *
 * @param <A> type of the values compared by a {@link Comparator}.
 */
public final class ComparatorStrategy<A> implements CompareStrategy<A> {
    private final Comparator<A> comparator;

    public ComparatorStrategy(final Comparator<A> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(final A first, final A second) {
        return comparator.compare(first, second);
    }
}