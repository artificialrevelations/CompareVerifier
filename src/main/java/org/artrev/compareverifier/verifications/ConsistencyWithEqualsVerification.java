package org.artrev.compareverifier.verifications;

import java.util.List;

public final class ConsistencyWithEqualsVerification<A> implements Verification<A> {
    private final Compare


    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances
    ) {
        private void verifyCompareToConsistentWithEquals(final List<A> instances) {
            final A instance = instances.get(0);
            for (final A a : instances) {
                final boolean equals = instance.equals(a);
                final boolean compareTo = compareStrategy.compareTo(instance, a) == 0;
                if (equals != compareTo)
                    throw new AssertionError("Comparing is not consistent with equals!");
            }
        }
    }
}
