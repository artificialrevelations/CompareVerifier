package org.artrev.compareverifier;

import org.artrev.compareverifier.strategies.CompareStrategy;
import org.artrev.compareverifier.verifications.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

final class ConfigurableVerifier<A> {
    private final VerificationInstancesProvider<A> lesserCreator;
    private final VerificationInstancesProvider<A> greaterCreator;
    private final VerificationInstancesProvider<A> equalCreator;
    private final CompareStrategy<A> strategy;

    private boolean suppressConsistentWithEquals = false;
    private boolean suppressEqualsToNullReturnsFalse = false;
    private boolean suppressExceptionOnCompareToNull = false;

    ConfigurableVerifier(final CompareStrategy<A> compareStrategy,
                         final VerificationInstancesProvider<A> lesserCreator,
                         final VerificationInstancesProvider<A> equalCreator,
                         final VerificationInstancesProvider<A> greaterCreator) {
        this.lesserCreator = lesserCreator;
        this.greaterCreator = greaterCreator;
        this.equalCreator = equalCreator;
        this.strategy = compareStrategy;
    }

    protected void suppressConsistentWithEquals(final boolean suppressCheck) {
        suppressConsistentWithEquals = suppressCheck;
    }

    protected void suppressExceptionOnCompareToNull(final boolean suppressCheck) {
        suppressExceptionOnCompareToNull = suppressCheck;
    }

    protected void suppressEqualsToNullReturnsFalse(final boolean suppressCheck) {
        suppressEqualsToNullReturnsFalse = suppressCheck;
    }

    protected void verify() {
        // verify that the instances creators are not null (obvious check)
        // verify that the instances List is not null (obvious check)
        // verify that the instances List has at least one element (obvious check)
        verifyInstancesProviders(lesserCreator, "lesser");
        verifyInstancesProviders(equalCreator, "equal");
        verifyInstancesProviders(greaterCreator, "greater");

        final List<A> lesserInstances = lesserCreator.create();
        final List<A> equalInstances = equalCreator.create();
        final List<A> greaterInstances = greaterCreator.create();

        final List<Verification<A>> verifications = new ArrayList<Verification<A>>();
        verifications.add(new InstancesListNotNullVerification<A>());
        verifications.add(new InstancesAreNotNullVerification<A>());
        verifications.add(new InstancesSizeVerification<A>());
        if (!suppressConsistentWithEquals)
            verifications.add(new ConsistencyWithEqualsVerification<A>(strategy));
        if (!suppressEqualsToNullReturnsFalse)
            verifications.add(new EqualityWithNullVerification<A>());

        for (final Verification<A> verification : verifications) {
            verification.verify(
                    lesserInstances,
                    equalInstances,
                    greaterInstances
            );
        }

        // verify that the returned instances throw an exception when compared to null
        verifyExceptionOnComparingWithNull(lesserInstances);
        verifyExceptionOnComparingWithNull(equalInstances);
        verifyExceptionOnComparingWithNull(greaterInstances);

        // verify that sgn(a.compareTo(b)) == -sgn(b.compareTo(a))
        verifyReverse(equalInstances, equalInstances);
        verifyReverse(equalInstances, lesserInstances);
        verifyReverse(equalInstances, greaterInstances);
        verifyReverse(lesserInstances, greaterInstances);

        // verify that sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0
        verifyTransitivity(lesserInstances, equalInstances, greaterInstances);

        // TODO: test sgn(a.compareTo(c)) == sgn(b.compareTo(c)) => sgn(a.compareTo(b)) == 0
    }

    // sgn(a.compareTo(b)) > 0 && sgn(b.compareTo(c)) > 0 => sgn(a.compareTo(c)) > 0
    private void verifyTransitivity(final List<A> lesser,
                                    final List<A> equal,
                                    final List<A> greater) {
        for (final A la : lesser) {
            for (final A ea : equal) {
                for (final A ga : greater) {
                    final int equal_lesser = (int) Math.signum(strategy.compare(ea, la));
                    final int greater_equal = (int) Math.signum(strategy.compare(ga, ea));
                    final int greater_lesser = (int) Math.signum(strategy.compare(ga, la));

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
                    signOfAtoB = (int) Math.signum(strategy.compare(a, b));
                } catch (final Exception exc) {
                    exceptionOnAtoBCompare = true;
                }

                // getting info for sgn(b.compareTo(a)) part
                int signOfBtoA = Integer.MAX_VALUE;
                boolean exceptionOnBtoACompare = false;
                try {
                    signOfBtoA = (int) Math.signum(strategy.compare(b, a));
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

    @SuppressWarnings({"ConstantConditions"})
    private void verifyExceptionOnComparingWithNull(final List<A> instances) {
        if (suppressExceptionOnCompareToNull)
            return;

        for (final A instance : instances) {
            boolean contractIsBroken;
            try {
                strategy.compare(instance, null);
                contractIsBroken = true;
            } catch (final Exception exc) {
                // this should throw an exception
                continue;
            }

            if (contractIsBroken)
                throw new AssertionError("Comparing to null should throw an exception!");
        }
    }

    private static <A> void verifyInstancesProviders(final VerificationInstancesProvider<A> provider,
                                                     final String instancesType) {
        if (null == provider)
            throw new IllegalArgumentException(
                    String.format(
                            "Provider for %s instances cannot be null!",
                            instancesType
                    )
            );
    }
}
