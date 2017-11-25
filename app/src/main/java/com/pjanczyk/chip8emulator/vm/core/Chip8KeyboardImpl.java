package com.pjanczyk.chip8emulator.vm.core;

import com.pjanczyk.chip8emulator.vm.Chip8Keyboard;

class Chip8KeyboardImpl implements Chip8Keyboard {

    private int keyState;

    public boolean isAnyKeyPressed() {
        return keyState != 0;
    }

    public boolean isKeyPressed(int key) {
        return (keyState & (1 << key)) != 0;
    }

    @Override
    public void setKeyPressed(int key, boolean pressed) {
        if (pressed) {
            keyState |= (1 << key);
        } else {
            keyState &= ~(1 << key);
        }
    }

    @Override
    public void reset() {
        keyState = 0;
    }
}
