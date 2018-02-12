package com.pjanczyk.chip8emulator.vm;

import java.util.Arrays;

class Chip8Display implements Chip8ReadOnlyDisplay {

    private static final int DISPLAY_WIDTH = 64;
    private static final int DISPLAY_HEIGHT = 32;

    private final boolean[] data = new boolean[DISPLAY_WIDTH * DISPLAY_HEIGHT];

    @Override
    public int getWidth() {
        return DISPLAY_WIDTH;
    }

    @Override
    public int getHeight() {
        return DISPLAY_HEIGHT;
    }

    @Override
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

    public boolean[] getState() {
        return data.clone();
    }

    public void restoreState(boolean[] state) {
        System.arraycopy(state, 0, data, 0, data.length);
    }
}
