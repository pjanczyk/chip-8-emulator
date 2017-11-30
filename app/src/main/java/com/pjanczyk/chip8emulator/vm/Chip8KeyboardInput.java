package com.pjanczyk.chip8emulator.vm;

public interface Chip8KeyboardInput {

    void setKeyPressed(int key, boolean pressed);

    void reset();
}
