package com.pjanczyk.chip8emulator.vm;

public interface Chip8ReadOnlyDisplay {
    int getWidth();
    int getHeight();
    boolean getPixel(int x, int y);
}
