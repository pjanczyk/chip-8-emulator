package com.pjanczyk.chip8emulator.vm;// Author: Piotr Janczyk, 27.03.16

public interface Chip8Keyboard {
    void setKeyPressed(int key, boolean pressed);
    void reset();
}
