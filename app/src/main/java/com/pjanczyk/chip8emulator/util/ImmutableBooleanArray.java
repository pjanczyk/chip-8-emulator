package com.pjanczyk.chip8emulator.util;

import com.google.common.primitives.Booleans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ImmutableBooleanArray {
    private final boolean[] array;

    public static ImmutableBooleanArray copyOf(boolean[] values) {
        return new ImmutableBooleanArray(Arrays.copyOf(values, values.length));
    }

    private ImmutableBooleanArray(boolean[] array) {
        this.array = array;
    }

    public int length() {
        return array.length;
    }

    public boolean get(int index) {
        return array[index];
    }

    public boolean[] toArray() {
        return Arrays.copyOf(array, array.length);
    }

    public List<Boolean> asList() {
        return Collections.unmodifiableList(Booleans.asList(array));
    }
}
