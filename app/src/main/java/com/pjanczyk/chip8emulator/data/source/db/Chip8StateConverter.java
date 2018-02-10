package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.TypeConverter;

import com.annimon.stream.Stream;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pjanczyk.chip8emulator.vm.Chip8State;

public class Chip8StateConverter {
    @TypeConverter
    public static Chip8State fromBytes(byte[] value) {
        if (value == null) return null;
        ByteArrayDataInput input = ByteStreams.newDataInput(value);

        short PC = input.readShort();
        ImmutableList<Byte> V = ImmutableList.copyOf(
                Stream.generate(input::readByte).limit(Chip8State.V_SIZE).iterator()
        );
        short I = input.readShort();
        ImmutableList<Byte> memory = ImmutableList.copyOf(
                Stream.generate(input::readByte).limit(Chip8State.MEMORY_SIZE).iterator()
        );
        ImmutableList<Short> stack = ImmutableList.copyOf(
                Stream.generate(input::readShort).limit(input.readInt()).iterator()
        );
        byte delayTimer = input.readByte();
        byte soundTimer = input.readByte();
        ImmutableList<Boolean> display = ImmutableList.copyOf(
                Stream.generate(input::readBoolean).limit(Chip8State.DISPLAY_SIZE).iterator()
        );

        return new Chip8State(PC, V, I, memory, stack, delayTimer, soundTimer, display);
    }

    @TypeConverter
    public static byte[] toBytes(Chip8State value) {
        if (value == null) return null;
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeShort(value.PC);
        Stream.of(value.V).forEach(output::writeByte);
        output.writeShort(value.I);
        Stream.of(value.memory).forEach(output::writeByte);
        output.writeInt(value.stack.size());
        Stream.of(value.stack).forEach(output::writeShort);
        output.writeByte(value.delayTimer);
        output.writeByte(value.soundTimer);
        Stream.of(value.display).forEach(output::writeBoolean);

        return output.toByteArray();
    }

}
