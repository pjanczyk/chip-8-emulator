package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.TypeConverter;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.ImmutableIntArray;

public class ImmutableArrayConverter {
    @TypeConverter
    public static ImmutableIntArray fromBytes(byte[] value) {
        if (value == null) return null;
        ByteArrayDataInput input = ByteStreams.newDataInput(value);
        return ImmutableIntArray.copyOf(
                IntStream.generate(input::readInt)
                        .limit(value.length / 4)
                        .toArray()
        );
    }

    @TypeConverter
    public static byte[] toBytes(ImmutableIntArray value) {
        if (value == null) return null;
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        Stream.of(value.asList()).forEach(output::writeInt);
        return output.toByteArray();
    }
}
