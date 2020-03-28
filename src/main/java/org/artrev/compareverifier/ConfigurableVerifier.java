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
package org.artrev.compareverifier;

import org.artrev.compareverifier.strategies.CompareStrategy;
import org.artrev.compareverifier.verifications.*;

import java.util.ArrayList;
import java.util.List;

final class ConfigurableVerifier<A> {
    private final VerificationInstancesProvider<A> lesserCreator;
    private final VerificationInstancesProvider<A> greaterCreator;
    private final VerificationInstancesProvider<A> equalCreator;
    private final CompareStrategy<A> strategy;

    private boolean suppressConsistentWithEquals = false;
    private boolean suppressEqualsToNullReturnsFalse = false;
    private boolean suppressExceptionOnCompareToNull = false;

    ConfigurableVerifier(final CompareStrategy<A> compareStrategy,
                         final VerificationInstancesProvider<A> lesserCreator,
                         final VerificationInstancesProvider<A> equalCreator,
                         final VerificationInstancesProvider<A> greaterCreator) {
        this.lesserCreator = lesserCreator;
        this.greaterCreator = greaterCreator;
        this.equalCreator = equalCreator;
        this.strategy = compareStrategy;
    }

    protected void suppressConsistentWithEquals(final boolean suppressCheck) {
        suppressConsistentWithEquals = suppressCheck;
    }

    protected void suppressExceptionOnCompareToNull(final boolean suppressCheck) {
        suppressExceptionOnCompareToNull = suppressCheck;
    }

    protected void suppressEqualsToNullReturnsFalse(final boolean suppressCheck) {
        suppressEqualsToNullReturnsFalse = suppressCheck;
    }

    protected void verify() {
        // verify that the instances creators are not null (obvious check)
        // verify that the instances List is not null (obvious check)
        // verify that the instances List has at least one element (obvious check)
        verifyInstancesProviders(lesserCreator, "lesser");
        verifyInstancesProviders(equalCreator, "equal");
        verifyInstancesProviders(greaterCreator, "greater");

        final List<A> lesserInstances = lesserCreator.create();
        final List<A> equalInstances = equalCreator.create();
        final List<A> greaterInstances = greaterCreator.create();

        final List<Verification<A>> verifications = new ArrayList<Verification<A>>();

        verifications.add(new InstancesListNotNullVerification<A>());
        verifications.add(new InstancesAreNotNullVerification<A>());
        verifications.add(new InstancesSizeVerification<A>());

        if (!suppressConsistentWithEquals)
            verifications.add(new ConsistencyWithEqualsVerification<A>(strategy));

        if (!suppressEqualsToNullReturnsFalse)
            verifications.add(new EqualityWithNullVerification<A>());

        if (!suppressExceptionOnCompareToNull)
            verifications.add(new ComparisonWithNullVerification<A>(strategy));

        verifications.add(new ComparisonConnexityVerification<A>(strategy));
        verifications.add(new ComparisonTransitivityVerification<A>(strategy));

        for (final Verification<A> verification : verifications) {
            verification.verify(
                    lesserInstances,
                    equalInstances,
                    greaterInstances
            );
        }
        // TODO: Antisymmetry test
        // TODO: test sgn(a.compareTo(c)) == sgn(b.compareTo(c)) => sgn(a.compareTo(b)) == 0
    }

    private static <A> void verifyInstancesProviders(final VerificationInstancesProvider<A> provider,
                                                     final String instancesType) {
        if (null == provider)
            throw new IllegalArgumentException(
                    String.format(
                            "Provider for %s instances cannot be null!",
                            instancesType
                    )
            );
    }
}
