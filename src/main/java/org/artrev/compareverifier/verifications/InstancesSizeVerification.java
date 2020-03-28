package org.artrev.compareverifier.verifications;

import java.util.List;

public final class InstancesSizeVerification<A> implements Verification<A> {
    @Override
    public void verify(
            final List<A> lesserInstances,
            final List<A> equalInstances,
            final List<A> greaterInstances) {

        verifySizeIsAtLeast(lesserInstances, 1, "lesser");
        verifySizeIsAtLeast(equalInstances, 2, "equal");
        verifySizeIsAtLeast(greaterInstances, 1, "greater");
    }

    private static <A> void verifySizeIsAtLeast(
            final List<A> instances,
            final int minimalSize,
            final String instancesType
    ) {
        if (instances.size() < minimalSize)
            throw new AssertionError(
                    String.format(
                            "Provided %s instances cannot have less elements then %d!",
                            instancesType,
                            minimalSize
                    )
            );
    }
}
