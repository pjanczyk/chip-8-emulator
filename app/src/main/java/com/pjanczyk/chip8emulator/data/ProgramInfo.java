package com.pjanczyk.chip8emulator.data;

import java.util.Date;

/**
 * {@link ProgramInfo} without bytecode.
 */
public class ProgramInfo {
    private final int id;
    private final String name;
    private final boolean isBuiltIn;
    private final String author;
    private final String releaseDate;
    private final String description;
    private final Date lastOpenedAt;

    public ProgramInfo(int id, String name, boolean isBuiltIn,
                       String author, String releaseDate, String description,
                       Date lastOpenedAt) {
        this.id = id;
        this.name = name;
        this.isBuiltIn = isBuiltIn;
        this.author = author;
        this.releaseDate = releaseDate;
        this.description = description;
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

    public Date getLastOpenedAt() {
        return lastOpenedAt;
    }
}
