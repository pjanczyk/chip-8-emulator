package com.pjanczyk.chip8emulator.data.source;

import android.support.annotation.CheckResult;

import com.pjanczyk.chip8emulator.data.Program;
import com.pjanczyk.chip8emulator.data.ProgramInfo;
import com.pjanczyk.chip8emulator.data.source.db.ProgramDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Singleton
public class ProgramRepository {
    private final ProgramDao programDao;

    @Inject
    public ProgramRepository(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @CheckResult
    public Flowable<List<ProgramInfo>> getBuiltInPrograms() {
        return programDao.getBuiltInPrograms();
    }

    @CheckResult
    public Flowable<List<ProgramInfo>> getRecentPrograms(int limit) {
        return programDao.getRecentPrograms(limit);
    }

    @CheckResult
    public Maybe<Program> getProgram(int programId) {
        return programDao.getProgramById(programId);
    }

    @CheckResult
    public Completable addProgram(Program program) {
        return Completable.fromAction(() -> programDao.insertProgram(program));
    }

    @CheckResult
    public Completable updateProgram(Program program) {
        return Completable.fromAction(() -> programDao.updateProgram(program));
    }
}
