package com.pjanczyk.chip8emulator.util;

public class IntSizeUtil {

    public static int require8Bit(int val, String message) {
        if ((val | 0xFF) != 0xFF)
            throw new IllegalArgumentException(message);
        return val;
    }

    public static int require24Bit(int val, String message) {
        if ((val | 0xFF_FF_FF) != 0xFF_FF_FF)
            throw new IllegalArgumentException(message);
        return val;
    }
}
