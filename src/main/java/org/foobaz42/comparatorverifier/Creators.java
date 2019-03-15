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
package org.foobaz42.comparatorverifier;

public final class Creators {
    public static <A> Creator<A> always(final A value) {
        return new Creator<A>() {
            @Override
            public A create() {
                return value;
            }
        };
    }

    public static <A> Creator<A> nullValue() {
        return always(null);
    }
}