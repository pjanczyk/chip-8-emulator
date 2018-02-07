package com.pjanczyk.chip8emulator.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@SuppressWarnings("unused")
public class BuiltInProgram {
    @Attribute
    public String path;
    @Element
    public String title;
    @Element(required = false)
    public String description;
    @Element(required = false)
    public String author;
    @Element(required = false)
    public String releaseDate;
}
