package com.pjanczyk.chip8emulator.vm;

import android.support.annotation.Size;

import com.google.common.collect.ImmutableList;

public class Chip8State {
    public static final int V_SIZE = 16;
    public static final int MEMORY_SIZE = 4096;
    public static final int DISPLAY_SIZE = 2048;

    /* Program counter, 16-bit */
    public final short PC;
    /* Registers, 16 x 8-bit */
    public final @Size(V_SIZE) ImmutableList<Byte> V;
    /* Register, 16-bit */
    public final short I;
    /* Memory, 4096 x 8-bit */
    public final @Size(MEMORY_SIZE) ImmutableList<Byte> memory;
    /* Stack, variable size x 16-bit */
    public final ImmutableList<Short> stack;
    /* Delay timer, 8-bit */
    public final byte delayTimer;
    /* Sound timer, 8-bit */
    public final byte soundTimer;
    /* Display, 2048 x 1-bit */
    public final @Size(DISPLAY_SIZE) ImmutableList<Boolean> display;

    public Chip8State(short PC,
                      @Size(V_SIZE) ImmutableList<Byte> V,
                      short I,
                      @Size(MEMORY_SIZE) ImmutableList<Byte> memory,
                      ImmutableList<Short> stack,
                      byte delayTimer,
                      byte soundTimer,
                      @Size(DISPLAY_SIZE) ImmutableList<Boolean> display) {

        if (V.size() != V_SIZE) {
            throw new IllegalArgumentException("V..size() != " + V_SIZE);
        }
        if (memory.size() != MEMORY_SIZE) {
            throw new IllegalArgumentException("memory.size() != " + MEMORY_SIZE);
        }
        if (display.size() != DISPLAY_SIZE) {
            throw new IllegalArgumentException("display.size() != " + DISPLAY_SIZE);
        }

        this.PC = PC;
        this.V = V;
        this.I = I;
        this.memory = memory;
        this.stack = stack;
        this.delayTimer = delayTimer;
        this.soundTimer = soundTimer;
        this.display = display;
    }
}
