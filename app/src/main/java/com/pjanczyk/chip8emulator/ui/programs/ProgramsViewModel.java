package com.pjanczyk.chip8emulator.ui.programs;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.ViewModel;

import com.google.common.collect.ImmutableList;
import com.pjanczyk.chip8emulator.data.Program;
import com.pjanczyk.chip8emulator.data.source.ProgramRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class ProgramsViewModel extends ViewModel {
    private static final int RECENT_PROGRAMS_LIMIT = 5;

    private final ProgramRepository repository;
    private final LiveData<List<ProgramGroup>> programGroups;

    @Inject
    public ProgramsViewModel(ProgramRepository repository) {
        this.repository = repository;

        Flowable<ProgramGroup> recent = repository.getRecentPrograms(RECENT_PROGRAMS_LIMIT)
                .map(programs -> new ProgramGroup("Recent programs", programs));

        Flowable<ProgramGroup> builtIn = repository.getBuiltInPrograms()
                .map(programs -> new ProgramGroup("Built-in programs", programs));

        programGroups = LiveDataReactiveStreams.fromPublisher(
                Flowable.combineLatest(recent, builtIn, ImmutableList::of));
    }

    public LiveData<List<ProgramGroup>> getProgramGroups() {
        return programGroups;
    }

    public void addImportedProgram(String name, byte[] bytecode) {
        Program program = new Program.Builder()
                .setName(name)
                .setBuiltIn(false)
                .setBytecode(bytecode)
                .build();

        repository.addProgram(program)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
