package com.pjanczyk.chip8emulator.model.xml;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class BuiltInProgramList {
    @ElementList(inline = true, type = BuiltInProgram.class, entry = "program")
    public List<BuiltInProgram> programs;
}
