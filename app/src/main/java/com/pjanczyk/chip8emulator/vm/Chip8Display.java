package com.pjanczyk.chip8emulator.vm;

import com.pjanczyk.chip8emulator.util.ImmutableBooleanArray;

import java.util.Arrays;

class Chip8Display implements Chip8ReadOnlyDisplay {

    private static final int DISPLAY_WIDTH = 64;
    private static final int DISPLAY_HEIGHT = 32;

    private final boolean[] data = new boolean[DISPLAY_WIDTH * DISPLAY_HEIGHT];

    public int getWidth() {
        return DISPLAY_WIDTH;
    }

    public int getHeight() {
        return DISPLAY_HEIGHT;
    }

    public boolean getPixel(int x, int y) {
        if (x < 0 || x >= DISPLAY_WIDTH || y < 0 || y > DISPLAY_HEIGHT) {
            throw new IllegalArgumentException();
        }

        return data[DISPLAY_WIDTH * y + x];
    }

    public void setPixel(int x, int y, boolean on) {
        if (x < 0 || x >= DISPLAY_WIDTH || y < 0 || y > DISPLAY_HEIGHT) {
            throw new IllegalArgumentException();
        }

        data[DISPLAY_WIDTH * y + x] = on;
    }

    public void clear() {
        Arrays.fill(data, false);
    }

    public ImmutableBooleanArray getState() {
        return ImmutableBooleanArray.copyOf(data);
    }

    public void restoreState(ImmutableBooleanArray state) {
        System.arraycopy(state.toArray(), 0, data, 0, data.length);
    }
}
