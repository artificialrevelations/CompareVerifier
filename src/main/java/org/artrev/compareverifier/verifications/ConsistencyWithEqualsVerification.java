package org.artrev.compareverifier.verifications;

import org.artrev.compareverifier.strategies.CompareStrategy;

import java.util.List;

public final class ConsistencyWithEqualsVerification<A> implements Verification<A> {
    private final CompareStrategy<A> strategy;

    public ConsistencyWithEqualsVerification(final CompareStrategy<A> strategy) {
        this.strategy = strategy;
    }

    @Override
    public void verify(
            final List<A> __,
            final List<A> equalInstances,
            final List<A> ___
    ) {
        // verify that the returned instances are consistent with equals
        // we only check the instances created by the Equal instances creator
        // as they are supposed to be the same in terms of equals implementation
        verifyCompareToConsistentWithEquals(equalInstances);
    }

    private void verifyCompareToConsistentWithEquals(final List<A> instances) {
        final A instance = instances.get(0);
        for (final A a : instances) {
            final boolean equals = instance.equals(a);
            final boolean compareTo = strategy.compareTo(instance, a) == 0;
            if (equals != compareTo)
                throw new AssertionError("Consistency with equals missing!");
        }
    }
}
