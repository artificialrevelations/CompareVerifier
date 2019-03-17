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

import java.util.List;

public final class ComparableVerifier<A extends Comparable<A>> {
    private final VerificationInstancesCreator<A> lesserCreator;
    private final VerificationInstancesCreator<A> greaterCreator;
    private final VerificationInstancesCreator<A> equalCreator;

    private boolean suppressConsistentWithEquals = false;
    private boolean suppressEqualsToNullReturnsFalse = false;
    private boolean suppressExceptionOnCompareToNull = false;

    private ComparableVerifier(final VerificationInstancesCreator<A> lesserCreator,
                               final VerificationInstancesCreator<A> equalCreator,
                               final VerificationInstancesCreator<A> greaterCreator) {
        this.lesserCreator = lesserCreator;
        this.greaterCreator = greaterCreator;
        this.equalCreator = equalCreator;
    }

    public static <A extends Comparable<A>> ComparableVerifier<A> forInstances(
            final VerificationInstancesCreator<A> lesserCreator,
            final VerificationInstancesCreator<A> equalCreator,
            final VerificationInstancesCreator<A> greaterCreator) {

        return new ComparableVerifier<A>(
                lesserCreator, equalCreator, greaterCreator
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

    public ComparableVerifier<A> suppressEqualsToNullReturnsFalse(final boolean suppressCheck) {
        suppressEqualsToNullReturnsFalse = suppressCheck;
        return this;
    }

    public void verify() {
        // verify that the instances creators are not null (obvious check)
        verifyInstancesCreatorIsNotNull(lesserCreator, "lesser");
        verifyInstancesCreatorIsNotNull(equalCreator, "equal");
        verifyInstancesCreatorIsNotNull(greaterCreator, "greater");

        // verify that the instances List is not null (obvious check)
        final List<A> lessInstances =
                verifyInstancesIsNotNull(lesserCreator, "lesser");
        final List<A> equalInstances =
                verifyInstancesIsNotNull(equalCreator, "equal");
        final List<A> greaterInstances =
                verifyInstancesIsNotNull(greaterCreator, "greater");

        // verify that the returned instances are consistent with equals
        // we only check the instances created by the Equal instances creator
        // as they are supposed to be the same in terms of equals implementation
        verifyCompareToConsistentWithEquals(equalInstances);

        // verify that the returned instances return false when checked for equality
        // with null
        verifyEqualsToNullReturnsFalse(lessInstances);
        verifyEqualsToNullReturnsFalse(equalInstances);
        verifyEqualsToNullReturnsFalse(greaterInstances);

        // verify that the returned instances throw an exception when compared
        // to null
        verifyExceptionOnCompareToNull(lessInstances);
        verifyExceptionOnCompareToNull(equalInstances);
        verifyExceptionOnCompareToNull(greaterInstances);

        // verify that sgn(a.compareTo(b)) == -sgn(b.compareTo(a))
        verifyReverse(equalInstances, equalInstances);
        verifyReverse(equalInstances, lessInstances);
        verifyReverse(equalInstances, greaterInstances);
        verifyReverse(lessInstances, greaterInstances);

        // TODO: testing transitivity:
        // TODO: test sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0
        // TODO: test sgn(a.compareTo(c)) == sgn(b.compareTo(c)) => sgn(a.compareTo(b)) == 0
    }

    // sgn(a.compareTo(b)) == -sgn(b.compareTo(a))
    // TODO: a.compareTo(b) should throw exception iff b.compareTo(a) throws
    private void verifyReverse(final List<A> first,
                               final List<A> second) {
        for (A fa : first) {
            for (final A sa : second) {
                final int a_b = Math2.sgn(fa.compareTo(sa));
                final int b_a = -Math2.sgn(sa.compareTo(fa));
                if (a_b != b_a)
                    //TODO: revise all the exception messages, add more detail?
                    throw new AssertionError("Instances do not implement a total order!");
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void verifyEqualsToNullReturnsFalse(final List<A> instances) {
        if (suppressEqualsToNullReturnsFalse)
            return;

        for (final A instance : instances) {
            if (instance.equals(null)) {
                throw new AssertionError("Instance is equal to null!");
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    private void verifyExceptionOnCompareToNull(final List<A> instances) {
        if (suppressExceptionOnCompareToNull)
            return;

        for (final A instance : instances) {
            boolean contractIsBroken;
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

    private void verifyCompareToConsistentWithEquals(final List<A> instances) {
        if (suppressConsistentWithEquals)
            return;

        final A instance = instances.get(0);
        for (final A a : instances) {
            final boolean equals = instance.equals(a);
            final boolean compareTo = instance.compareTo(a) == 0;
            if (equals != compareTo)
                throw new AssertionError("CompareTo is not consistent with equals!");
        }
    }

    private static <A> void verifyInstancesCreatorIsNotNull(final VerificationInstancesCreator<A> creator,
                                                            final String type) {
        if (null == creator)
            throw new IllegalArgumentException("VerificationInstancesCreator (" + type + ") cannot be null!");
    }

    private static <A> List<A> verifyInstancesIsNotNull(final VerificationInstancesCreator<A> creator,
                                                        final String type) {
        final List<A> instances = creator.create();
        if (null == instances)
            throw new IllegalArgumentException("VerificationInstancesCreator (" + type + ") cannot return null instances!");
        return instances;
    }
}
