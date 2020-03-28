package org.artrev.compareverifier;

import org.artrev.compareverifier.strategies.ComparatorStrategy;

import java.util.Comparator;

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
 * {@code ComparableVerifier} works in tandem with {@link VerificationInstancesProvider}
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
 * @see VerificationInstancesProvider
 * @see VerificationInstancesCreators
 */
public class ComparatorVerifier<A, B extends Comparator<A>> {

    private final ConfigurableVerifier<A> verifier;

    private ComparatorVerifier(final B comparator,
                               final VerificationInstancesProvider<A> lesserCreator,
                               final VerificationInstancesProvider<A> equalCreator,
                               final VerificationInstancesProvider<A> greaterCreator) {
        verifier = new ConfigurableVerifier<A>(
                new ComparatorStrategy<A>(comparator),
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
    public static <A, B extends Comparator<A>> ComparatorVerifier<A, B> forInstances(
            final B comparator,
            final VerificationInstancesProvider<A> lesserCreator,
            final VerificationInstancesProvider<A> equalCreator,
            final VerificationInstancesProvider<A> greaterCreator) {

        return new ComparatorVerifier<A, B>(
                comparator, lesserCreator, equalCreator, greaterCreator
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
    public ComparatorVerifier<A, B> suppressConsistentWithEquals(final boolean suppressCheck) {
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
    public ComparatorVerifier<A, B> suppressExceptionOnCompareToNull(final boolean suppressCheck) {
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
    public ComparatorVerifier<A, B> suppressEqualsToNullReturnsFalse(final boolean suppressCheck) {
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