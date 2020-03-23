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
package org.artrev.compareverifier;

/**
 * {@code ComparableVerifier} is a tool that can be used in unit tests to verify
 * if certain implementation of the {@link Comparable} interface is correct.
 * <p>
 * By default verifier performs following checks if the implementation:
 * <ul>
 * <li>satisfies {@code sgn(a.compareTo(b)) == -sgn(b.compareTo(a))}</li>
 * <li>satisfies {@code sgn(a.compareTo(c)) == sgn(b.compareTo(c)) => sgn(a.compareTo(b)) == 0}</li>
 * <li>satisfies {@code sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0}</li>
 * <li>is consistent with equals</li>
 * </ul>
 * <p>
 * If any of the above checks fails then an {@link AssertionError} will be thrown
 * with an appropriate information about the cause.
 * <p>
 * {@code ComparableVerifier} works in tandem with {@link VerificationInstancesCreator}
 * which describes a factory for the instances used in testing. Verifier needs
 * three kinds of instances:
 * <ul>
 * <li><b>equal instances</b> - at least two objects that are equal but are not
 * of the same reference</li>
 * <li><b>lesser instances</b> - at least one (the more the better) object that is
 * "lesser" then the object(s) returned by the <b>equal instances</b> and by the
 * <b>greater instances</b></li>
 * <li><b>greater instances</b> - at least one (the more the better) object that
 * is "greater" then the object(s) returned by the <b>equal instances</b> and
 * by the <b>less instances</b></li>
 * </ul>
 * <p>
 * <b>Example Instance Creation:</b>
 * <pre>
 * {@code
 * final VerificationInstancesCreator<BigDecimal> lesserCreator =
 *         VerificationInstancesCreators.from(
 *             new BigDecimal("0.0"), // smaller then the objects returned by
 *             new BigDecimal("1.0"), // both equal creator and greater creator
 *             new BigDecimal("2.0")
 *         );
 * final VerificationInstancesCreator<BigDecimal> equalCreator =
 *         VerificationInstancesCreators.from(
 *             new BigDecimal("42.0"),
 *             new BigDecimal("42.0")
 *         );
 * final VerificationInstancesCreator<BigDecimal> greaterCreator =
 *         VerificationInstancesCreators.from(
 *             new BigDecimal("101.0"), // larger then the objects returned by
 *             new BigDecimal("202.0"), // both equal creator and lesser creator
 *             new BigDecimal("303.0")
 *         );
 * }
 * </pre>
 * The more instances the creators are able to produce the better.
 * <p>
 * <b>Basic Usage:</b>
 * <pre>
 * {@code
 * ComparableVerifier
 *     .forInstances(lesserCreator, equalCreator, greaterCreator)
 *     .verify();
 * }
 * </pre>
 * It's not advised, but possible to suppress one or more of the checks with
 * builder methods.
 * <p>
 * <b>Example Suppress:</b>
 * <pre>
 * {@code
 * ComparableVerifier
 *     .forInstances(lesserCreator, equalCreator, greaterCreator)
 *     .suppressConsistentWithEquals(true)
 *     .verify();
 * }
 * </pre>
 * Please be aware that some of the checks done by this class expect that the
 * instances have a {@link Object#toString()} implementation. This is very
 * important as it is used for creating assertion messages.
 *
 * @param <A> type of the class under test
 * @see Comparable
 * @see VerificationInstancesCreator
 * @see VerificationInstancesCreators
 */
public final class ComparableVerifier<A extends Comparable<A>> {

    private final ConfigurableVerifier<A> verifier;

    private ComparableVerifier(final VerificationInstancesCreator<A> lesserCreator,
                               final VerificationInstancesCreator<A> equalCreator,
                               final VerificationInstancesCreator<A> greaterCreator) {
        verifier = new ConfigurableVerifier<A>(
                new ComparableStrategy<A>(),
                lesserCreator,
                equalCreator,
                greaterCreator
        );
    }

    /**
     * Creates an instance of the {@link ComparableVerifier}.
     *
     * @param lesserCreator  "lesser" instances factory
     * @param equalCreator   "equal" instances factory
     * @param greaterCreator "greater" instances factory
     * @param <A>            type of the class under test
     * @return instance of {@link ComparableVerifier}
     */
    public static <A extends Comparable<A>> ComparableVerifier<A> forInstances(
            final VerificationInstancesCreator<A> lesserCreator,
            final VerificationInstancesCreator<A> equalCreator,
            final VerificationInstancesCreator<A> greaterCreator) {

        return new ComparableVerifier<A>(
                lesserCreator, equalCreator, greaterCreator
        );
    }

    /**
     * Causes that the (a.compareTo(b)==0) == (a.equals(b)) won't be verified.
     * <p>
     * According to the {@link Comparable} documentation it is strongly
     * recommended, but not strictly required that aforementioned rule is obeyed.
     * <p>
     * It is advised that any class that implements the {@link Comparable}
     * interface and violates this particular rule should clearly indicate this
     * fact.
     *
     * @param suppressCheck true if the rule should be suppressed.
     * @return instance of {@link ComparableVerifier}
     */
    public ComparableVerifier<A> suppressConsistentWithEquals(final boolean suppressCheck) {
        verifier.suppressConsistentWithEquals(suppressCheck);
        return this;
    }

    /**
     * Causes that the check if a.compareTo(null) throws an exception won't be
     * verified.
     * <p>
     * Null is not an instance of any class thus a.compareTo(null) should throw
     * a {@link NullPointerException} even though a.equals(null) returns false.
     *
     * @param suppressCheck true if the rule should be suppressed.
     * @return instance of {@link ComparableVerifier}
     */
    public ComparableVerifier<A> suppressExceptionOnCompareToNull(final boolean suppressCheck) {
        verifier.suppressExceptionOnCompareToNull(suppressCheck);
        return this;
    }

    /**
     * Causes that the check if a.equals(null) returns false won't be verified.
     * For more information please check {@link #suppressExceptionOnCompareToNull(boolean)}
     *
     * @param suppressCheck true if the rule should be suppressed.
     * @return instance of {@link ComparableVerifier}
     */
    public ComparableVerifier<A> suppressEqualsToNullReturnsFalse(final boolean suppressCheck) {
        verifier.suppressEqualsToNullReturnsFalse(suppressCheck);
        return this;
    }

    /**
     * Performs verification if the tested instances are in natural order thus
     * the {@link Comparable} interfaces is correctly implemented.
     */
    public void verify() {
        verifier.verify();
    }
}
