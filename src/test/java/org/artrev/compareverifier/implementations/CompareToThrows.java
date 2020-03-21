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

public class CompareToThrows extends Correct {
    public CompareToThrows(final int value) {
        super(value);
    }

    @Override
    public int compareTo(final Correct other) {
        throw new CompareToException("A possible exception that might happen in compareTo!");
    }

    @Override
    public String toString() {
        return String.format("CompareToThrows{ value = %d }", value);
    }

    public static class CompareToException extends RuntimeException {
        public CompareToException(final String message) {
            super(message);
        }
    }
}
