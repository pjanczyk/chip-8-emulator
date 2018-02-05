package com.pjanczyk.chip8emulator.ui.emulator;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.repository.ProgramRepository;

import javax.inject.Inject;

public class EmulatorViewModel extends ViewModel {
    private final ProgramRepository repository;
    private LiveData<Program> program;

    @Inject
    public EmulatorViewModel(ProgramRepository repository) {
        this.repository = repository;
    }

    public void init(int programId) {
        this.program = LiveDataReactiveStreams.fromPublisher(repository.getProgram(programId));
    }

    public LiveData<Program> getProgram() {
        return program;
    }
}
