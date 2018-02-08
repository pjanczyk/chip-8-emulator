package com.pjanczyk.chip8emulator.ui.programs;

import com.pjanczyk.chip8emulator.data.ProgramInfo;

import java.util.List;

public class ProgramGroup {

    public final String name;
    public final List<ProgramInfo> programs;

    public ProgramGroup(String name, List<ProgramInfo> programs) {
        this.name = name;
        this.programs = programs;
    }
}
