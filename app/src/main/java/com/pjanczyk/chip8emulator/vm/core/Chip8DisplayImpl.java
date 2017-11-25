package com.pjanczyk.chip8emulator.vm.core;// Author: Piotr Janczyk, 26.03.16

import com.pjanczyk.chip8emulator.vm.Chip8Display;

import java.util.Arrays;

class Chip8DisplayImpl implements Chip8Display {

    private static final int DISPLAY_COLUMNS = 8;
    private static final int DISPLAY_ROWS = 32;

    private final byte[] data = new byte[DISPLAY_COLUMNS * DISPLAY_ROWS];

    @Override
    public int getWidth() {
        return DISPLAY_COLUMNS * 8;
    }

    @Override
    public int getHeight() {
        return DISPLAY_ROWS;
    }

    @Override
    public boolean getPixel(int x, int y) {
        int column = x / 8;
        int bit = 7 - (x % 8);

        byte b = data[DISPLAY_COLUMNS * y + column];

        return (b & (1 << bit)) != 0;
    }

    public void clear() {
        Arrays.fill(data, (byte) 0);
    }

    public boolean drawSprite(int x, int y, byte[] array, int offset, int length) {
        boolean anyErased = false;

        for (int i = 0; i < length; i++) {
            anyErased |= drawByte(x, y + i, array[offset + i]);
        }

        return anyErased;
    }

    private boolean drawByte(int x, int y, byte b) {
        int offset = x % 8;
        int b1 = (b & 0xFF) >>> offset;
        int b2 = (b & 0xFF) << (8 - offset);

        int row = y % DISPLAY_ROWS;
        int column1 = (x / 8) % DISPLAY_COLUMNS;
        int column2 = (x / 8 + 1) % DISPLAY_COLUMNS;

        int index1 = DISPLAY_COLUMNS * row + column1;
        int index2 = DISPLAY_COLUMNS * row + column2;

        boolean anyErased = (data[index1] & b1) != 0 || (data[index2] & b2) != 0;

        data[index1] ^= b1;
        data[index2] ^= b2;

        return anyErased;
    }
}
