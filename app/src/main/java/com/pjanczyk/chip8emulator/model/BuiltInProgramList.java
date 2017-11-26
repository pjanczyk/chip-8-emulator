package com.pjanczyk.chip8emulator.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
class BuiltInProgramList {

    @ElementList(inline = true, type = BuiltInProgram.class, entry = "program")
    private List<Program> programs;

    private BuiltInProgramList() {
    }

    public List<Program> getPrograms() {
        return programs;
    }
}
