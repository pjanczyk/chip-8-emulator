package com.pjanczyk.chip8emulator.model.repository;

import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.ProgramInfo;
import com.pjanczyk.chip8emulator.model.db.ProgramDao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Singleton
public class ProgramRepository {
    private final ProgramDao programDao;

    @Inject
    public ProgramRepository(ProgramDao programDao) {
        this.programDao = programDao;
    }

    public Flowable<List<ProgramInfo>> getBuiltInPrograms() {
        return programDao.getBuiltInPrograms();
    }

    public Flowable<List<ProgramInfo>> getRecentPrograms(int limit) {
        return programDao.getRecentPrograms(limit);
    }

    public Single<Program> getProgram(int programId) {
        return programDao.getProgramById(programId);
    }

    public void addProgram(Program program) {
        programDao.insertProgram(program);
    }

    public void updateLastOpenedAt(int programId, Date lastOpenedAt) {
        programDao.updateLastOpenedAt(programId, lastOpenedAt);
    }

}
