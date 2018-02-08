package com.pjanczyk.chip8emulator.data.source.xml;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@SuppressWarnings("unused")
@Root
public class BuiltInProgramList {
    @ElementList(inline = true, type = BuiltInProgram.class, entry = "program")
    public List<BuiltInProgram> programs;
}
