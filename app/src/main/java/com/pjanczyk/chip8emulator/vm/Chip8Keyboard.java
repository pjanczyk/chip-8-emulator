package com.pjanczyk.chip8emulator.vm;

class Chip8Keyboard implements Chip8KeyboardInput {

    private volatile int keyState;
    private volatile int keyPressed = -1;

    public boolean isKeyPressed(int key) {
        return (keyState & (1 << key)) != 0;
    }

    public int readKeyPressed() {
        int tmp = keyPressed;
        keyPressed = -1;
        return tmp;
    }

    public void setKeyPressed(int key, boolean pressed) {
        if (pressed) {
            keyState |= (1 << key);
            keyPressed = key;
        } else {
            keyState &= ~(1 << key);
        }
    }

    public void reset() {
        keyState = 0;
    }
}
