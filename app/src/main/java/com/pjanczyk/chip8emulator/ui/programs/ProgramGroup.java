package com.pjanczyk.chip8emulator.ui.programs;// Author: Piotr Janczyk, 28.03.16

import com.pjanczyk.chip8emulator.model.Program;

import java.util.List;

public class ProgramGroup {

    public final String name;
    public final List<Program> programs;

    public ProgramGroup(String name, List<Program> programs) {
        this.name = name;
        this.programs = programs;
    }
}
