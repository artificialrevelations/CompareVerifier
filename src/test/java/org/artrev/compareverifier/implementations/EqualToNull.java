package org.artrev.compareverifier.implementations;

public class EqualToNull extends Correct {
    public EqualToNull(final int value) {
        super(value);
    }

    @Override
    public boolean equals(final Object other) {
        if (null == other)
            return true; // obvious mistake!!

        return super.equals(other);
    }

    @Override
    public String toString() {
        return String.format("EqualToNull{ value = %d }", value);
    }
}
