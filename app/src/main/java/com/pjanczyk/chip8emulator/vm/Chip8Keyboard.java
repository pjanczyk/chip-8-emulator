package com.pjanczyk.chip8emulator.vm;

public class Chip8Keyboard {

    private int keyState;

    public boolean isAnyKeyPressed() {
        return keyState != 0;
    }

    public boolean isKeyPressed(int key) {
        return (keyState & (1 << key)) != 0;
    }

    public void setKeyPressed(int key, boolean pressed) {
        if (pressed) {
            keyState |= (1 << key);
        } else {
            keyState &= ~(1 << key);
        }
    }

    public void reset() {
        keyState = 0;
    }
}
