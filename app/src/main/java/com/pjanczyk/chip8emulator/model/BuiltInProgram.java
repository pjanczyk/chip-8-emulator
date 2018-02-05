package com.pjanczyk.chip8emulator.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class BuiltInProgram {
    @Attribute
    private String path;
    @Element
    private String title;
    @Element(required = false)
    private String description;
    @Element(required = false)
    private String author;
    @Element(required = false)
    private String releaseDate;

    private BuiltInProgram() {}

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
