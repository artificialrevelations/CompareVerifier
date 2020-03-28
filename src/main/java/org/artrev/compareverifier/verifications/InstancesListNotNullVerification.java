package org.artrev.compareverifier.verifications;

import java.util.List;

/**
 * @param <A>
 */
public final class InstancesListNotNullVerification<A> implements Verification<A> {
    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances
    ) {
        verifyNotNull(lesserInstances, "lesser");
        verifyNotNull(equalInstances, "equal");
        verifyNotNull(greaterInstances, "greater");
    }

    private static <A> void verifyNotNull(
            final List<A> instances,
            final String instancesType
    ) {
        if (null == instances)
            throw new AssertionError(
                    String.format(
                            "Provided %s instances cannot be null!",
                            instancesType
                    )
            );
    }
}
