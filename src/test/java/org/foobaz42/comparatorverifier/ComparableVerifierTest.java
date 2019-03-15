package org.foobaz42.comparatorverifier;

import org.foobaz42.comparatorverifier.implementations.Correct;
import org.foobaz42.comparatorverifier.implementations.InconsistentWithEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(Enclosed.class)
public class ComparableVerifierTest {
    public static class SuppressConsistentWithEquals {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void set_to_true_should_suppress_equals_consistency_check() {
            // given:
            final Creator<Correct> less =
                    Creators.always(new InconsistentWithEquals(0, 30));
            final Creator<Correct> equal =
                    Creators.always(new InconsistentWithEquals(42, 30));
            final Creator<Correct> greater =
                    Creators.always(new InconsistentWithEquals(100, 30));

            final ComparableVerifier<Correct> tested =
                    ComparableVerifier.forInstances(less, equal, greater)
                            .suppressConsistentWithEquals(true);

            // when:
            tested.verify();
        }

        @Test
        public void set_to_false_should_not_suppress_equals_consistency_check() {
            // given:
            final Creator<Correct> less =
                    Creators.always(new InconsistentWithEquals(0, 30));
            final Creator<Correct> equal =
                    Creators.always(new InconsistentWithEquals(42, 30));
            final Creator<Correct> greater =
                    Creators.always(new InconsistentWithEquals(100, 30));

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("CompareTo is not consistent with equals!");

            final ComparableVerifier<Correct> tested =
                    ComparableVerifier.forInstances(less, equal, greater)
                            .suppressConsistentWithEquals(false);

            // when:
            tested.verify();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static class ForInstance {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void should_throw_If_Less_Creator_is_Null() {
            // given:
            final Creator<Correct> less = null;
            final Creator<Correct> equal =
                    Creators.always(new Correct(42));
            final Creator<Correct> greater =
                    Creators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("Less creator cannot be null!");

            // when:
            ComparableVerifier.forInstances(less, equal, greater);
        }

        @Test
        public void should_throw_If_Equal_Creator_is_Null() {
            // given:
            final Creator<Correct> less =
                    Creators.always(new Correct(0));
            final Creator<Correct> equal = null;
            final Creator<Correct> greater =
                    Creators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("Equal creator cannot be null!");

            // when:
            ComparableVerifier.forInstances(less, equal, greater);
        }

        @Test
        public void should_throw_If_Greater_Creator_is_Null() {
            // given:
            final Creator<Correct> less =
                    Creators.always(new Correct(0));
            final Creator<Correct> equal =
                    Creators.always(new Correct(42));
            final Creator<Correct> greater = null;

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("Greater creator cannot be null!");

            // when:
            ComparableVerifier.forInstances(less, equal, greater);
        }

        @Test
        public void should_create_Verifier_instance() {
            // given:
            final Creator<Correct> less =
                    Creators.always(new Correct(0));
            final Creator<Correct> equal =
                    Creators.always(new Correct(42));
            final Creator<Correct> greater =
                    Creators.always(new Correct(100));

            // when:
            final ComparableVerifier<Correct> verifier =
                    ComparableVerifier.forInstances(less, equal, greater);

            // then:
            assertNotNull(verifier);
        }
    }

    public static class SuppressExceptionOnNullInstance {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void should_throw_if_not_suppressed() {
            // given:
            final Creator<Correct> less =
                    Creators.nullValue();
            final Creator<Correct> equal =
                    Creators.always(new Correct(42));
            final Creator<Correct> greater =
                    Creators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("Creator should not create null instances!");

            // when:
            ComparableVerifier
                    .forInstances(less, equal, greater)
                    .suppressExceptionOnNullInstance(false)
                    .verify();
        }
    }
}

