package com.pjanczyk.chip8emulator.data.source;

import android.support.annotation.Nullable;

import java.util.Map;

public class KeyBinding {
    public final Map<String, String> map;

    public KeyBinding(Map<String, String> map) {
        this.map = map;
    }

    @Nullable
    public String getKeyDescription(int key) {
        return map.get(Integer.toString(key));
    }
}
