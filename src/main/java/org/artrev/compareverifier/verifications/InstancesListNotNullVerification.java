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
