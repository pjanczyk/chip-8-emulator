package com.pjanczyk.chip8emulator.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(indices = {
        @Index("name"),
        @Index(value = "bytecode", unique = true),
        @Index("lastOpenedAt")
})
public class Program {
    @PrimaryKey(autoGenerate = true)
    private final int id;
    private final String name;
    private final boolean isBuiltIn;
    private final String author;
    private final String releaseDate;
    private final String description;
    private final byte[] bytecode;
    private final Date lastOpenedAt;

    public Program(int id, String name, boolean isBuiltIn,
                   String author, String releaseDate, String description,
                   byte[] bytecode, Date lastOpenedAt) {
        this.id = id;
        this.name = name;
        this.isBuiltIn = isBuiltIn;
        this.author = author;
        this.releaseDate = releaseDate;
        this.description = description;
        this.bytecode = bytecode;
        this.lastOpenedAt = lastOpenedAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isBuiltIn() {
        return isBuiltIn;
    }

    public String getAuthor() {
        return author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getBytecode() {
        return bytecode;
    }

    public Date getLastOpenedAt() {
        return lastOpenedAt;
    }
}
