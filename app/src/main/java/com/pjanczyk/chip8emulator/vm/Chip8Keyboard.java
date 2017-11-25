package com.pjanczyk.chip8emulator.vm;

public interface Chip8Keyboard {
    void setKeyPressed(int key, boolean pressed);
    void reset();
}
