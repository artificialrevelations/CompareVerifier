package org.foobaz42.compareverifier.implementations;

public class CompareToNull extends Correct {
    public CompareToNull(final int value) {
        super(value);
    }

    @Override
    public int compareTo(final Correct other) {
        // this simulates an issue where compareTo(null) won't throw an exception
        if (null == other)
            return 0;

        return super.compareTo(other);
    }
}
