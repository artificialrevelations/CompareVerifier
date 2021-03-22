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

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

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
    private final VerificationInstancesCreator<A> lesserCreator;
    private final VerificationInstancesCreator<A> greaterCreator;
    private final VerificationInstancesCreator<A> equalCreator;

    private boolean suppressConsistentWithEquals = false;
    private boolean suppressEqualsToNullReturnsFalse = false;
    private boolean suppressExceptionOnCompareToNull = false;

    private ComparableVerifier(final VerificationInstancesCreator<A> lesserCreator,
                               final VerificationInstancesCreator<A> equalCreator,
                               final VerificationInstancesCreator<A> greaterCreator) {
        this.lesserCreator = lesserCreator;
        this.greaterCreator = greaterCreator;
        this.equalCreator = equalCreator;
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
        suppressConsistentWithEquals = suppressCheck;
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
        suppressExceptionOnCompareToNull = suppressCheck;
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
        suppressEqualsToNullReturnsFalse = suppressCheck;
        return this;
    }

    /**
     * Performs verification if the tested instances are in natural order thus
     * the {@link Comparable} interfaces is correctly implemented.
     */
    public void verify() {
        // verify that the instances creators are not null (obvious check)
        // verify that the instances List is not null (obvious check)
        // verify that the instances List has at least one element (obvious check)
        final List<A> lesserInstances =
                verifyInstancesCreator(lesserCreator, "lesser");
        final List<A> equalInstances =
                verifyInstancesCreator(equalCreator, "equal");
        final List<A> greaterInstances =
                verifyInstancesCreator(greaterCreator, "greater");

        // verify that the returned instances are consistent with equals
        // we only check the instances created by the Equal instances creator
        // as they are supposed to be the same in terms of equals implementation
        verifyCompareToConsistentWithEquals(equalInstances);

        // verify that the returned instances return false when checked for equality with null
        verifyEqualsToNullReturnsFalse(lesserInstances);
        verifyEqualsToNullReturnsFalse(equalInstances);
        verifyEqualsToNullReturnsFalse(greaterInstances);

        // verify that the returned instances throw an exception when compared to null
        verifyExceptionOnCompareToNull(lesserInstances);
        verifyExceptionOnCompareToNull(equalInstances);
        verifyExceptionOnCompareToNull(greaterInstances);

        // verify that sgn(a.compareTo(b)) == -sgn(b.compareTo(a))
        verifyReverse(equalInstances, equalInstances);
        verifyReverse(equalInstances, lesserInstances);
        verifyReverse(equalInstances, greaterInstances);
        verifyReverse(lesserInstances, greaterInstances);

        // verify that sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0
        verifyTransitivity(lesserInstances, equalInstances, greaterInstances);
        // verify that sgn(a.compareTo(c)) == 0 && sgn(b.compareTo(c)) == 0 => sgn(a.compareTo(b)) == 0
        verifyEqualsTransitivity(equalInstances);
    }

    private void verifyEqualsTransitivity(List<A> equalInstances) {
        if (equalInstances.size() < 3) {
            ArrayList<A> target = new ArrayList<A>();
            while (target.size() < 3) target.addAll(equalInstances);
            checkTriples(target);
        } else {
            checkTriples(equalInstances);
        }
    }

    private void checkTriples(List<A> equalInstances) {
        for (int i = 0; i < equalInstances.size(); i++) {
            for (int j = i + 1; j < equalInstances.size(); j++) {
                for (int k = j + 1; k < equalInstances.size(); k++) {
                    A first = equalInstances.get(i);
                    A second = equalInstances.get(j);
                    A third = equalInstances.get(k);
                    checkTransitiveEqualityOfTriple(first, second, third);
                }
            }
        }
    }

    private void checkTransitiveEqualityOfTriple(A first, A second, A third) {
        if (first.compareTo(third) != 0)
            throw new AssertionError(format("items %s and %s are not equal while should be", first, third));
        if (second.compareTo(third) != 0)
            throw new AssertionError(format("items %s and %s are not equal while should be", first, third));
        if (first.compareTo(second) != 0)
            throw new AssertionError(
                    format("Equality is not transitive: %s equals to %s, %s equals to %s, but %s is not equal to %s",
                            first,
                            third,
                            second,
                            third,
                            first,
                            second));
    }

    // sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0
    private void verifyTransitivity(final List<A> lesser,
                                    final List<A> equal,
                                    final List<A> greater) {
        for (final A la : lesser) {
            for (final A ea : equal) {
                for (final A ga : greater) {
                    final int equal_lesser = (int) Math.signum(ea.compareTo(la));
                    final int greater_equal = (int) Math.signum(ga.compareTo(ea));
                    final int greater_lesser = (int) Math.signum(ga.compareTo(la));

                    final boolean isTransitive =
                            equal_lesser > 0
                                    && greater_equal > 0
                                    && greater_lesser > 0;

                    if (!isTransitive) {
                        throw new AssertionError(
                                format("Instances %s, %s, %s, are not transitive!", la, ea, ga)
                        );
                    }
                }
            }
        }
    }

    // sgn(a.compareTo(b)) == -sgn(b.compareTo(a))
    // a.compareTo(b) should throw exception iff b.compareTo(a) throws
    private void verifyReverse(final List<A> first,
                               final List<A> second) {
        for (final A a : first) {
            for (final A b : second) {
                // getting info for sgn(a.compareTo(b)) part
                int signOfAtoB = Integer.MIN_VALUE;
                boolean exceptionOnAtoBCompare = false;
                try {
                    signOfAtoB = (int) Math.signum(a.compareTo(b));
                } catch (final Exception exc) {
                    exceptionOnAtoBCompare = true;
                }

                // getting info for sgn(b.compareTo(a)) part
                int signOfBtoA = Integer.MAX_VALUE;
                boolean exceptionOnBtoACompare = false;
                try {
                    signOfBtoA = (int) Math.signum(b.compareTo(a));
                } catch (final Exception exc) {
                    exceptionOnBtoACompare = true;
                }

                // if the a.compareTo(b) threw an exception but b.compareTo(a) did not
                if (exceptionOnAtoBCompare && !exceptionOnBtoACompare) {
                    throw new AssertionError(
                            format("Comparing %s to %s threw an exception but %s to %s did not!", a, b, b, a)
                    );
                }
                // if the b.compareTo(a) threw an exception but a.compareTo(b) did not
                if (!exceptionOnAtoBCompare && exceptionOnBtoACompare) {
                    throw new AssertionError(
                            format("Comparing %s to %s threw an exception but %s to %s did not!", b, a, a, b)
                    );
                }
                // if sgn(a.compareTo(b)) != -sgn(b.compareTo(a))
                if (signOfAtoB != -signOfBtoA) {
                    throw new AssertionError("Instances do not implement a total order!");
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void verifyEqualsToNullReturnsFalse(final List<A> instances) {
        if (suppressEqualsToNullReturnsFalse)
            return;

        for (final A instance : instances) {
            if (instance.equals(null)) {
                throw new AssertionError("Instance is equal to null!");
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    private void verifyExceptionOnCompareToNull(final List<A> instances) {
        if (suppressExceptionOnCompareToNull)
            return;

        for (final A instance : instances) {
            boolean contractIsBroken;
            try {
                instance.compareTo(null);
                contractIsBroken = true;
            } catch (final Exception exc) {
                // this should throw an exception
                continue;
            }

            if (contractIsBroken)
                throw new AssertionError("CompareTo null should throw an exception!");
        }
    }

    private void verifyCompareToConsistentWithEquals(final List<A> instances) {
        if (suppressConsistentWithEquals)
            return;

        final A instance = instances.get(0);
        for (final A a : instances) {
            final boolean equals = instance.equals(a);
            final boolean compareTo = instance.compareTo(a) == 0;
            if (equals != compareTo)
                throw new AssertionError("CompareTo is not consistent with equals!");
        }
    }

    private static <A> List<A> verifyInstancesCreator(final VerificationInstancesCreator<A> creator,
                                                      final String type) {
        if (null == creator)
            throw new IllegalArgumentException("VerificationInstancesCreator (" + type + ") cannot be null!");

        final List<A> instances = creator.create();
        if (null == instances)
            throw new IllegalArgumentException("VerificationInstancesCreator (" + type + ") cannot return null instances!");

        if (instances.isEmpty())
            throw new IllegalArgumentException("VerificationInstancesCreator (" + type + ") cannot return empty list of instances!");

        for (final A instance : instances) {
            if (null == instance)
                throw new IllegalArgumentException("VerificationInstancesCreator (" + type + ") cannot contain null instances!");
        }

        return instances;
    }
}
