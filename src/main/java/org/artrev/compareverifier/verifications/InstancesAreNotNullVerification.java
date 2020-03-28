package org.artrev.compareverifier.verifications;

import java.util.List;

public final class InstancesAreNotNullVerification<A> implements Verification<A> {
    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances
    ) {
        verifyInstancesAreNotNull(lesserInstances, "lesser");
        verifyInstancesAreNotNull(equalInstances, "equal");
        verifyInstancesAreNotNull(greaterInstances, "greater");
    }

    private static <A> void verifyInstancesAreNotNull(
            final List<A> instances,
            final String instancesType
    ) {
        for (final A instance : instances) {
            if (null == instance)
                throw new AssertionError(
                        String.format(
                                "Provided %s instances cannot contain null elements!",
                                instancesType
                        )
                );
        }
    }
}
