package com.pjanczyk.chip8emulator.ui.programs;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.common.collect.ImmutableList;
import com.pjanczyk.chip8emulator.Transformations2;
import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.repository.ProgramRepository;

import java.util.List;

import javax.inject.Inject;

public class ProgramsViewModel extends ViewModel {
    private static final int RECENT_PROGRAMS_LIMIT = 5;

    private final ProgramRepository repository;

    @Inject
    public ProgramsViewModel(ProgramRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ProgramGroup>> getProgramGroups() {
        return Transformations2.flatten(ImmutableList.of(
                Transformations.map(
                        repository.getRecentPrograms(RECENT_PROGRAMS_LIMIT),
                        programs -> new ProgramGroup("Recent programs", programs)
                ),
                Transformations.map(
                        repository.getBuiltInPrograms(),
                        programs1 -> new ProgramGroup("Built-in programs", programs1)
                )
        ));
    }

    public void addImportedProgram(String name, byte[] bytecode) {
        Program program = new Program(0, name, false, null, null,
                null, bytecode, null);

        repository.addProgram(program);
    }

}
