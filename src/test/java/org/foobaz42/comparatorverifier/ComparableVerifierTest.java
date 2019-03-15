package org.foobaz42.comparatorverifier;

import org.foobaz42.comparatorverifier.implementations.Correct;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(Enclosed.class)
public class ComparableVerifierTest {
    public static class ForInstance {
        @Rule
        public ExpectedException mExpectedException = ExpectedException.none();

        @Test
        public void should_throw_If_Less_Creator_is_Null() {
            // given:
            final Creator<Correct> lessInstanceCreator = null;
            final Creator<Correct> equalInstanceCreator =
                    Creators.always(new Correct(42));
            final Creator<Correct> greaterInstanceCreator =
                    Creators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("Less creator cannot be null!");

            // when:
            ComparableVerifier.forInstances(
                    lessInstanceCreator,
                    equalInstanceCreator,
                    greaterInstanceCreator
            );
        }

        @Test
        public void should_throw_If_Equal_Creator_is_Null() {
            // given:
            final Creator<Correct> lessInstanceCreator =
                    Creators.always(new Correct(0));
            final Creator<Correct> equalInstanceCreator = null;
            final Creator<Correct> greaterInstanceCreator =
                    Creators.always(new Correct(100));

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("Equal creator cannot be null!");

            // when:
            ComparableVerifier.forInstances(
                    lessInstanceCreator,
                    equalInstanceCreator,
                    greaterInstanceCreator
            );
        }

        @Test
        public void should_throw_If_Greater_Creator_is_Null() {
            // given:
            final Creator<Correct> lessInstanceCreator =
                    Creators.always(new Correct(0));
            final Creator<Correct> equalInstanceCreator =
                    Creators.always(new Correct(42));
            final Creator<Correct> greaterInstanceCreator = null;

            mExpectedException.expect(IllegalArgumentException.class);
            mExpectedException.expectMessage("Greater creator cannot be null!");

            // when:
            ComparableVerifier.forInstances(
                    lessInstanceCreator,
                    equalInstanceCreator,
                    greaterInstanceCreator
            );
        }

        @Test
        public void should_create_Verifier_instance() {
            // given:
            final Creator<Correct> lessInstanceCreator =
                    Creators.always(new Correct(0));
            final Creator<Correct> equalInstanceCreator =
                    Creators.always(new Correct(42));
            final Creator<Correct> greaterInstanceCreator =
                    Creators.always(new Correct(100));

            // when:
            final ComparableVerifier<Correct> verifier =
                    ComparableVerifier.forInstances(
                            lessInstanceCreator,
                            equalInstanceCreator,
                            greaterInstanceCreator
                    );

            // then:
            assertNotNull(verifier);
        }
    }

    public static class SuppressExceptionOnNullInstance {

    }
}

