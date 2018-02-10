package com.pjanczyk.chip8emulator.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

@Entity(indices = {
        @Index("name"),
        @Index(value = "bytecode", unique = true),
        @Index("lastOpenedAt")
})
public class Program {
    @PrimaryKey(autoGenerate = true)
    public final Integer id;
    @NonNull
    public final String name;
    @NonNull
    public final boolean isBuiltIn;
    @Nullable
    public final String author;
    @Nullable
    public final String releaseDate;
    @Nullable
    public final String description;
    @NonNull
    public final byte[] bytecode;
    @Nullable
    public final Date lastOpenedAt;

    public Program(Integer id,
                   @NonNull String name,
                   boolean isBuiltIn,
                   @Nullable String author,
                   @Nullable String releaseDate,
                   @Nullable String description,
                   @NonNull byte[] bytecode,
                   @Nullable Date lastOpenedAt) {
        this.id = id;
        this.name = name;
        this.isBuiltIn = isBuiltIn;
        this.author = author;
        this.releaseDate = releaseDate;
        this.description = description;
        this.bytecode = bytecode;
        this.lastOpenedAt = lastOpenedAt;
    }
}
