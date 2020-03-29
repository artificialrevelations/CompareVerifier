package org.artrev.compareverifier.verifications;

import org.artrev.compareverifier.implementations.Correct;
import org.artrev.compareverifier.strategies.ComparableStrategy;
import org.artrev.compareverifier.strategies.CompareStrategy;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(Enclosed.class)
public class ComparisonTransitivityVerificationTest {
    @RunWith(Theories.class)
    public static class NotTransitive {
        @Rule
        public ExpectedException expectedException =
                ExpectedException.none();

        @DataPoint
        public static CompareStrategy<Correct> comparableStrategy =
                new ComparableStrategy<Correct>();

        @Theory
        public void for_incorrect_lesser_instances(final CompareStrategy<Correct> strategy) {
            // given:
            final List<Correct> lesserInstances = Arrays.asList(
                    new Correct(0),
                    new Correct(43),
                    new Correct(103)
            );

            final List<Correct> equalInstances = Arrays.asList(
                    new Correct(42),
                    new Correct(42)
            );

            final List<Correct> greaterInstances = Arrays.asList(
                    new Correct(101),
                    new Correct(102)
            );

            final Verification<Correct> tested =
                    new ComparisonTransitivityVerification<Correct>(
                            strategy
                    );

            expectedException.expect(AssertionError.class);
            expectedException.expectMessage(CoreMatchers.containsString("are not transitive!"));
            // when:
            tested.verify(
                    lesserInstances,
                    equalInstances,
                    greaterInstances
            );
        }

        @Theory
        public void for_incorrect_greater_instances(final CompareStrategy<Correct> strategy) {
            // given:
            final List<Correct> lesserInstances = Arrays.asList(
                    new Correct(0),
                    new Correct(43),
                    new Correct(103)
            );

            final List<Correct> equalInstances = Arrays.asList(
                    new Correct(42),
                    new Correct(42)
            );

            final List<Correct> greaterInstances = Arrays.asList(
                    new Correct(101),
                    new Correct(102)
            );

            final Verification<Correct> tested =
                    new ComparisonTransitivityVerification<Correct>(
                            strategy
                    );

            expectedException.expect(AssertionError.class);
            expectedException.expectMessage(CoreMatchers.containsString("are not transitive!"));
            // when:
            tested.verify(
                    lesserInstances,
                    equalInstances,
                    greaterInstances
            );
        }
    }
}