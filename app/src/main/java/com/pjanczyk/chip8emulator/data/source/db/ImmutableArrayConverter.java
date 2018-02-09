package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.TypeConverter;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.ImmutableIntArray;
import com.pjanczyk.chip8emulator.util.ImmutableBooleanArray;

public class ImmutableArrayConverter {
    @TypeConverter
    public static ImmutableIntArray toImmutableIntArray(byte[] value) {
        if (value == null) return null;
        ByteArrayDataInput input = ByteStreams.newDataInput(value);
        return ImmutableIntArray.copyOf(
                IntStream.generate(input::readInt)
                        .limit(value.length / 4)
                        .toArray()
        );
    }

    @TypeConverter
    public static byte[] fromImmutableIntArray(ImmutableIntArray value) {
        if (value == null) return null;
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        Stream.of(value.asList()).forEach(output::writeInt);
        return output.toByteArray();
    }

    @TypeConverter
    public static ImmutableBooleanArray toImmutableBooleanArray(byte[] value) {
        if (value == null) return null;
        boolean[] array = new boolean[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = value[i] != 0;
        }
        return ImmutableBooleanArray.copyOf(array);
    }

    @TypeConverter
    public static byte[] fromImmutableBooleanArray(ImmutableBooleanArray value) {
        if (value == null) return null;
        byte[] array = new byte[value.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = value.get(i) ? (byte) 1 : (byte) 0;
        }
        return array;
    }
}
