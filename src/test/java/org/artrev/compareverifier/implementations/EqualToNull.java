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
package org.artrev.compareverifier.implementations;

public class EqualToNull extends Correct {
    public EqualToNull(final int value) {
        super(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (null == other)
            return true; // obvious mistake!!

        return super.equals(other);
    }

    @Override
    public String toString() {
        return String.format("EqualToNull{ value = %d }", value);
    }
}
