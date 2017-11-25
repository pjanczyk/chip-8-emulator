package com.pjanczyk.chip8emulator.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Collections;
import java.util.List;

@Root
public class BuiltinProgramList {

    @ElementList(inline = true, type = BuiltinProgram.class, entry = "program")
    private List<Program> programs;

    private BuiltinProgramList() {}

    public List<Program> getPrograms() {
        return Collections.unmodifiableList(programs);
    }
}
