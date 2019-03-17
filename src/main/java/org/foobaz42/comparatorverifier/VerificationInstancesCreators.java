/*
  Copyright (c) 2019-present, ComparatorVerifier Contributors.

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
package org.foobaz42.comparatorverifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A collection of functions for easy creation of {@link VerificationInstancesCreator}
 * instances.
 * <p>
 * <b>Example Usage:</b>
 * <pre>
 * {@code
 * final VerificationInstancesCreator<BigDecimal> lesserCreator =
 *         VerificationInstancesCreators.from(
 *             new BigDecimal("0.0"), // smaller then the objects returned by
 *             new BigDecimal("1.0"), // both equal creator and greater creator
 *             new BigDecimal("2.0")
 *         );
 * final VerificationInstancesCreator<BigDecimal> equalCreator =
 *         VerificationInstancesCreators.from(
 *             new BigDecimal("42.0"),
 *             new BigDecimal("42.0")
 *         );
 * final VerificationInstancesCreator<BigDecimal> greaterCreator =
 *         VerificationInstancesCreators.from(
 *             new BigDecimal("101.0"), // larger then the objects returned by
 *             new BigDecimal("202.0"), // both equal creator and lesser creator
 *             new BigDecimal("303.0")
 *         );
 * }
 *
 * @see VerificationInstancesCreator
 * @see ComparableVerifier
 */
public final class VerificationInstancesCreators {
    private VerificationInstancesCreators() {
        throw new IllegalStateException("VerificationInstancesCreators should not be instantiated!");
    }

    /**
     * Creates a {@link VerificationInstancesCreator} from the given array of
     * values.
     *
     * @param values values that should be returned by the Creator.
     * @param <A>    type of the instances returned by the Creator.
     * @param <B>    type of the instances returned by the Creator
     * @return instance of {@link VerificationInstancesCreator}
     */
    public static <A, B extends A> VerificationInstancesCreator<A> from(final B... values) {
        return new VerificationInstancesCreator<A>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<A> create() {
                return (List<A>) Arrays.asList(values);
            }
        };
    }

    /**
     * Creates a {@link VerificationInstancesCreator} that will return a List
     * with a null value.
     *
     * @param <A> type of the instances returned by the Creator.
     * @return instance of {@link VerificationInstancesCreator}
     */
    public static <A> VerificationInstancesCreator<A> nullValues() {
        return new VerificationInstancesCreator<A>() {
            @Override
            public List<A> create() {
                return Collections.singletonList(null);
            }
        };
    }

    /**
     * Creates a {@link VerificationInstancesCreator} that will return a null
     * List. This method is mainly handy for testing of the
     * {@link ComparableVerifier} implementation.
     *
     * @param <A> type of the instances returned by the Creator.
     * @return instance of {@link VerificationInstancesCreator}
     */
    public static <A> VerificationInstancesCreator<A> nullInstances() {
        return new VerificationInstancesCreator<A>() {
            @Override
            public List<A> create() {
                return null;
            }
        };
    }
}
