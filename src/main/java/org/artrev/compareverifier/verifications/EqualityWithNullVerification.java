package org.artrev.compareverifier.verifications;

import java.util.List;

public final class EqualityWithNullVerification<A> implements Verification<A> {
    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances
    ) {
        verifyEqualityWithNull(lesserInstances, "lesser");
        verifyEqualityWithNull(equalInstances, "equal");
        verifyEqualityWithNull(greaterInstances, "greater");
    }

    @SuppressWarnings("ConstantConditions")
    private static <A> void verifyEqualityWithNull(final List<A> instances,
                                                   final String instancesType) {
        for (final A instance : instances) {
            if (instance.equals(null)) {
                throw new AssertionError(
                        String.format(
                                "Provided %s instance: %s is equal to null!",
                                instancesType,
                                instance
                        )
                );
            }
        }
    }
}
