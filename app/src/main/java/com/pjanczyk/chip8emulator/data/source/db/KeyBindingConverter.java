package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.GsonBuilder;
import com.pjanczyk.chip8emulator.data.source.KeyBinding;

public class KeyBindingConverter {
    @TypeConverter
    public static KeyBinding fromJson(String value) {
        if (value == null) return null;
        return new GsonBuilder().create().fromJson(value, KeyBinding.class);
    }

    @TypeConverter
    public static String toJson(KeyBinding value) {
        if (value == null) return null;
        return new GsonBuilder().create().toJson(value, KeyBinding.class);
    }
}
