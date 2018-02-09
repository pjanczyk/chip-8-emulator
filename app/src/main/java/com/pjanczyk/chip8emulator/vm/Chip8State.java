package com.pjanczyk.chip8emulator.vm;

import com.google.common.primitives.ImmutableIntArray;
import com.pjanczyk.chip8emulator.util.ImmutableBooleanArray;

import static com.pjanczyk.chip8emulator.util.IntSizeUtil.require24Bit;
import static com.pjanczyk.chip8emulator.util.IntSizeUtil.require8Bit;

public class Chip8State {
    /* Program counter, 24-bit */
    public final int PC;
    /* Registers, 16 x 8-bit */
    public final ImmutableIntArray V;
    /* Register, 24-bit */
    public final int I;
    /* Memory, 4096 x 8-bit */
    public final ImmutableIntArray memory;
    /* Stack, variable size x 24-bit */
    public final ImmutableIntArray stack;
    /* Delay timer TODO: size */
    public final int delayTimer;
    /* Sound timer TODO: size */
    public final int soundTimer;
    /* Display, 2048 x 1-bit */
    public final ImmutableBooleanArray display;

    public Chip8State(int PC,
                      ImmutableIntArray V,
                      int I,
                      ImmutableIntArray memory,
                      ImmutableIntArray stack,
                      int delayTimer,
                      int soundTimer,
                      ImmutableBooleanArray display) {
        this.PC = require24Bit(PC, "PC is not 24-bit");

        if (V.length() != 16) throw new IllegalArgumentException("V.length != 16");
        for (int i = 0; i < V.length(); i++) {
            require8Bit(V.get(i), "V is not 8-bit array");
        }
        this.V = V;

        this.I = require24Bit(I, "I is not 24-bit");

        if (memory.length() != 4096) throw new IllegalArgumentException("memory.length != 4096");
        for (int i = 0; i < memory.length(); i++) {
            require8Bit(memory.get(i), "memory is not 8-bit array");
        }
        this.memory = memory;

        this.stack = stack;
        this.delayTimer = delayTimer; // TODO: size check
        this.soundTimer = soundTimer; // TODO: size check

        if (display.length() != 2048) throw new IllegalArgumentException("display.length != 2048");
        this.display = display;
    }
}
