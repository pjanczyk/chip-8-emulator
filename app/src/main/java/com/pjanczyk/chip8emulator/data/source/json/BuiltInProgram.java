package com.pjanczyk.chip8emulator.data.source.json;

import java.util.Map;

class BuiltInProgram {
    public final String name;
    public final String author;
    public final String date;
    public final String description;
    public final String path;
    public final Map<String, String> keys;

    public BuiltInProgram(String name,
                          String author,
                          String date,
                          String description,
                          String path,
                          Map<String, String> keys) {
        this.name = name;
        this.author = author;
        this.date = date;
        this.description = description;
        this.path = path;
        this.keys = keys;
    }
}
