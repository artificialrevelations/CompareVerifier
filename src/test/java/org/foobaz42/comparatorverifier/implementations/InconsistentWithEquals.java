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
package org.foobaz42.comparatorverifier.implementations;

//TODO: doc
// this is using compareTo from base
// but has its own equals defined
public class InconsistentWithEquals extends Correct {
    public final int another;

    public InconsistentWithEquals(final int value,
                                  final int another) {
        super(value);
        this.another = another;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other == null || getClass() != other.getClass())
            return false;

        if (!super.equals(other))
            return false;

        final InconsistentWithEquals that = (InconsistentWithEquals) other;

        return another == that.another;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + another;
        return result;
    }

    @Override
    public String toString() {
        return "InconsistentWithEquals{ another=" + another + ", value=" + value + '}';
    }
}
