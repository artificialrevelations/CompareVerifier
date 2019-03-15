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

import java.util.ArrayList;
import java.util.List;

public final class ComparableVerifier<A extends Comparable<A>> {
    private final Creator<A> lessCreator;
    private final Creator<A> greaterCreator;
    private final Creator<A> equalCreator;

    private boolean suppressConsistentWithEquals = false;
    private boolean suppressExceptionOnCompareToNull = false;

    private int amountOfChecks = 10;

    private ComparableVerifier(final Creator<A> lessCreator,
                               final Creator<A> equalCreator,
                               final Creator<A> greaterCreator) {
        this.lessCreator = lessCreator;
        this.greaterCreator = greaterCreator;
        this.equalCreator = equalCreator;
    }

    public static <A extends Comparable<A>> ComparableVerifier<A> forInstances(
            final Creator<A> lessCreator,
            final Creator<A> equalCreator,
            final Creator<A> greaterCreator) {

        if (null == lessCreator)
            throw new IllegalArgumentException("Less creator cannot be null!");

        if (null == equalCreator)
            throw new IllegalArgumentException("Equal creator cannot be null!");

        if (null == greaterCreator)
            throw new IllegalArgumentException("Greater creator cannot be null!");

        return new ComparableVerifier<A>(
                lessCreator, equalCreator, greaterCreator
        );
    }

    public ComparableVerifier<A> suppressConsistentWithEquals(final boolean suppressCheck) {
        suppressConsistentWithEquals = suppressCheck;
        return this;
    }

    public ComparableVerifier<A> suppressExceptionOnCompareToNull(final boolean suppressCheck) {
        suppressExceptionOnCompareToNull = suppressCheck;
        return this;
    }

    public ComparableVerifier<A> withAmountOfChecks(final int amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount of checks needs to be greater then zero!");
        amountOfChecks = amount;
        return this;
    }

    public void verify() {
        final List<A> lessInstances = getList(lessCreator, amountOfChecks);
        // we want at least two instances that are supposedly the same
        final List<A> equalInstances = getList(equalCreator, amountOfChecks + 1);
        final List<A> greaterInstances = getList(greaterCreator, amountOfChecks);

        verifyCompareToConsistentWithEquals(equalInstances);

        verifyExceptionOnCompareToNull(lessInstances);
        verifyExceptionOnCompareToNull(equalInstances);
        verifyExceptionOnCompareToNull(greaterInstances);

        verifyReverseSignumCase(equalInstances, equalInstances);
        verifyReverseSignumCase(equalInstances, lessInstances);
        verifyReverseSignumCase(equalInstances, greaterInstances);
        verifyReverseSignumCase(lessInstances, greaterInstances);

        //TODO: testing transitivity
        //TODO: test a.compareTo(c) == b.compareTo(c) => a.compareTo(b) == 0
    }

    // sig(a.compareTo(b)) == -sig(b.compareTo(a))
    private void verifyReverseSignumCase(final List<A> first,
                                         final List<A> second) {
        for (A fa : first) {
            for (final A sa : second) {
                final int a_b = Float.floatToIntBits(Math.signum(fa.compareTo(sa)));
                final int b_a = Float.floatToIntBits(Math.signum(sa.compareTo(fa)));
                if (a_b != -b_a)
                    throw new AssertionError("Instances are not truly equal!");
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    private void verifyExceptionOnCompareToNull(final List<A> instances) {
        if (suppressExceptionOnCompareToNull)
            return;

        for (final A instance : instances) {
            if (instance.equals(null)) {
                throw new AssertionError("Instance is equal to null!");
            }

            boolean contractIsBroken = false;
            try {
                instance.compareTo(null);
                contractIsBroken = true;
            } catch (final Exception exc) {
                // this should throw an exception
                continue;
            }

            if (contractIsBroken)
                throw new AssertionError("CompareTo null should throw an exception!");
        }
    }

    private void verifyCompareToConsistentWithEquals(final List<A> equalInstances) {
        if (suppressConsistentWithEquals)
            return;

        final A instance = equalInstances.get(0);
        for (final A a : equalInstances) {
            final boolean equals = instance.equals(a);
            final boolean compareTo = instance.compareTo(a) == 0;
            if (equals != compareTo)
                throw new AssertionError("Natural ordering is not consistent with equals!");
        }
    }

    private List<A> getList(final Creator<A> creator,
                            final int amount) {
        final List<A> instances = new ArrayList<A>(amount);
        for (int i = 0; i < amount; i++) {
            final A instance = creator.create();
            if (null == instance)
                throw new IllegalArgumentException("Creator should not create null instances!");

            instances.add(instance);
        }
        return instances;
    }
}
