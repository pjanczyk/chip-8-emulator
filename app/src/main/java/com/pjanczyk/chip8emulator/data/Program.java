package com.pjanczyk.chip8emulator.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pjanczyk.chip8emulator.data.source.KeyBinding;
import com.pjanczyk.chip8emulator.vm.Chip8State;

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
    public final Boolean isBuiltIn;
    @Nullable
    public final String author;
    @Nullable
    public final String releaseDate;
    @Nullable
    public final String description;
    @NonNull
    public final KeyBinding keyBinding;
    @NonNull
    public final byte[] bytecode;
    @Nullable
    public final Date lastOpenedAt;
    @Nullable
    public final Chip8State quickSave;

    public Program(@Nullable Integer id,
                   @NonNull String name,
                   @NonNull Boolean isBuiltIn,
                   @Nullable String author,
                   @Nullable String releaseDate,
                   @Nullable String description,
                   @NonNull KeyBinding keyBinding,
                   @NonNull byte[] bytecode,
                   @Nullable Date lastOpenedAt,
                   @Nullable Chip8State quickSave) {
        this.id = id;
        this.name = name;
        this.isBuiltIn = isBuiltIn;
        this.author = author;
        this.releaseDate = releaseDate;
        this.description = description;
        this.keyBinding = keyBinding;
        this.bytecode = bytecode;
        this.lastOpenedAt = lastOpenedAt;
        this.quickSave = quickSave;
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static class Builder {
        private Integer id = null;
        private String name = null;
        private Boolean isBuiltIn = null;
        private String author = null;
        private String releaseDate = null;
        private String description = null;
        private KeyBinding keyBinding = null;
        private byte[] bytecode = null;
        private Date lastOpenedAt = null;
        private Chip8State quickSave = null;

        public Builder() { }

        Builder(Program program) {
            id = program.id;
            name = program.name;
            isBuiltIn = program.isBuiltIn;
            author = program.author;
            releaseDate = program.releaseDate;
            description = program.description;
            bytecode = program.bytecode;
            lastOpenedAt = program.lastOpenedAt;
            quickSave = program.quickSave;
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setBuiltIn(boolean builtIn) {
            isBuiltIn = builtIn;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setKeyBinding(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
            return this;
        }

        public Builder setBytecode(byte[] bytecode) {
            this.bytecode = bytecode;
            return this;
        }

        public Builder setLastOpenedAt(Date lastOpenedAt) {
            this.lastOpenedAt = lastOpenedAt;
            return this;
        }

        public Builder setQuickSave(Chip8State quickSave) {
            this.quickSave = quickSave;
            return this;
        }

        public Program build() {
            if (name == null || isBuiltIn == null || bytecode == null || keyBinding == null)
                throw new IllegalStateException("name, isBuiltIn, bytecode, keyBinding must be set");

            return new Program(id, name, isBuiltIn, author, releaseDate, description,
                    keyBinding, bytecode, lastOpenedAt, quickSave);
        }

    }
}
