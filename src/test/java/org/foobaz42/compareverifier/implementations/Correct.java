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
package org.foobaz42.compareverifier.implementations;

public class Correct implements Comparable<Correct> {
    public final int value;

    public Correct(final int value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (null == other || getClass() != other.getClass())
            return false;

        final Correct that = (Correct) other;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public int compareTo(final Correct other) {
        return value - other.value;
    }

    @Override
    public String toString() {
        return "Correct{ value=" + value + '}';
    }
}
