package org.foobaz42.comparatorverifier.implementations;

public class CompareToNull extends Correct {
    public CompareToNull(final int value) {
        super(value);
    }

    @Override
    public int compareTo(final Correct other) {
        if (null == other)
            return 0;

        return super.compareTo(other);
    }
}
