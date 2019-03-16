package org.foobaz42.comparatorverifier;

import org.foobaz42.comparatorverifier.implementations.CompareToNull;
import org.foobaz42.comparatorverifier.implementations.Correct;
import org.foobaz42.comparatorverifier.implementations.EqualToNull;
import org.foobaz42.comparatorverifier.implementations.InconsistentWithEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(Enclosed.class)
public class ComparableVerifierTest {
    public static class ForInstance {
        @Test
        public void should_create_non_Null_Verifier_instance() {
            // given:
            final VerificationInstancesCreator<Correct> less =
                    VerificationInstancesCreators.always(new Correct(0));
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(new Correct(42));
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(new Correct(100));

            // when:
            final ComparableVerifier<Correct> verifier =
                    ComparableVerifier.forInstances(less, equal, greater);

            // then:
            assertNotNull(verifier);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static class ArgumentVerification {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void should_throw_If_Lesser_Creator_is_Null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser = null;
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(new Correct(42));
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("VerificationInstancesCreator (lesser) cannot be null!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .verify();
        }

        @Test
        public void should_throw_If_Equal_Creator_is_Null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(new Correct(0));
            final VerificationInstancesCreator<Correct> equal = null;
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("VerificationInstancesCreator (equal) cannot be null!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .verify();
        }

        @Test
        public void should_throw_If_Greater_Creator_is_Null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(new Correct(0));
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(new Correct(42));
            final VerificationInstancesCreator<Correct> greater = null;

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("VerificationInstancesCreator (greater) cannot be null!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .verify();
        }

        @Test
        public void should_throw_If_created_Lesser_instances_is_Null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.nullInstances();
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(new Correct(42));
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("VerificationInstancesCreator (lesser) cannot return null instances!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .verify();
        }

        @Test
        public void should_throw_If_created_Equal_instances_is_Null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(new Correct(0));
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.nullInstances();
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("VerificationInstancesCreator (equal) cannot return null instances!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .verify();
        }

        @Test
        public void should_throw_If_created_Greater_instances_is_Null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(new Correct(0));
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(new Correct(42));
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.nullInstances();

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("VerificationInstancesCreator (greater) cannot return null instances!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .verify();
        }
    }

    public static class SuppressConsistentWithEquals {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void should_allow_inconsistent_instances() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new InconsistentWithEquals(42, 0),
                            new InconsistentWithEquals(42, 1)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressConsistentWithEquals(true)
                    .verify();
        }

        @Test
        public void should_NOT_allow_inconsistent_instances() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new InconsistentWithEquals(42, 0),
                            new InconsistentWithEquals(42, 1)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("CompareTo is not consistent with equals!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressConsistentWithEquals(false)
                    .verify();
        }
    }

    public static class SuppressEqualsToNullReturnsFalse {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void should_allow_Lesser_instances_equal_to_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new EqualToNull(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressEqualsToNullReturnsFalse(true)
                    .verify();
        }

        @Test
        public void should_allow_Equal_instances_equal_to_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new EqualToNull(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressEqualsToNullReturnsFalse(true)
                    .verify();
        }

        @Test
        public void should_allow_Greater_instances_equal_to_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new EqualToNull(100)
                    );

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressEqualsToNullReturnsFalse(true)
                    .verify();
        }

        @Test
        public void should_NOT_allow_Lesser_instances_equal_to_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new EqualToNull(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("Instance is equal to null!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressEqualsToNullReturnsFalse(false)
                    .verify();
        }

        @Test
        public void should_NOT_allow_Equal_instances_equal_to_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new EqualToNull(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("Instance is equal to null!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressEqualsToNullReturnsFalse(false)
                    .verify();
        }

        @Test
        public void should_NOT_allow_Greater_instances_equal_to_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new EqualToNull(100)
                    );

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("Instance is equal to null!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressEqualsToNullReturnsFalse(false)
                    .verify();
        }
    }

    public static class SuppressExceptionOnCompareToNull {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void should_allow_Lesser_instances_compareTo_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new CompareToNull(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressExceptionOnCompareToNull(true)
                    .verify();
        }

        @Test
        public void should_allow_Equal_instances_compareTo_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new CompareToNull(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressExceptionOnCompareToNull(true)
                    .verify();
        }

        @Test
        public void should_allow_Greater_instances_compareTo_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new CompareToNull(100)
                    );

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressExceptionOnCompareToNull(true)
                    .verify();
        }

        @Test
        public void should_NOT_allow_Lesser_instances_compareTo_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new CompareToNull(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("CompareTo null should throw an exception!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressExceptionOnCompareToNull(false)
                    .verify();
        }

        @Test
        public void should_NOT_allow_Equal_instances_compareTo_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new CompareToNull(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new Correct(100)
                    );

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("CompareTo null should throw an exception!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressExceptionOnCompareToNull(false)
                    .verify();
        }

        @Test
        public void should_NOT_allow_Greater_instances_compareTo_null() {
            // given:
            final VerificationInstancesCreator<Correct> lesser =
                    VerificationInstancesCreators.always(
                            new Correct(0)
                    );
            final VerificationInstancesCreator<Correct> equal =
                    VerificationInstancesCreators.always(
                            new Correct(42)
                    );
            final VerificationInstancesCreator<Correct> greater =
                    VerificationInstancesCreators.always(
                            new CompareToNull(100)
                    );

            mExpectedException.expect(AssertionError.class);
            mExpectedException.expectMessage("CompareTo null should throw an exception!");

            // when:
            ComparableVerifier.forInstances(lesser, equal, greater)
                    .suppressExceptionOnCompareToNull(false)
                    .verify();
        }
    }
}

