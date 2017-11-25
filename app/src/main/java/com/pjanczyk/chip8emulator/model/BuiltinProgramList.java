package com.pjanczyk.chip8emulator.model;// Author: Piotr Janczyk, 29.03.16

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
