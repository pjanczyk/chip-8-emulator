package com.pjanczyk.chip8emulator.data.source;

import com.pjanczyk.chip8emulator.data.Program;
import com.pjanczyk.chip8emulator.data.ProgramInfo;
import com.pjanczyk.chip8emulator.data.Save;
import com.pjanczyk.chip8emulator.data.SaveInfo;
import com.pjanczyk.chip8emulator.data.source.db.ProgramDao;
import com.pjanczyk.chip8emulator.data.source.db.SaveDao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Singleton
public class ProgramRepository {
    private final ProgramDao programDao;
    private final SaveDao saveDao;

    @Inject
    public ProgramRepository(ProgramDao programDao, SaveDao saveDao) {
        this.programDao = programDao;
        this.saveDao = saveDao;
    }

    public Flowable<List<ProgramInfo>> getBuiltInPrograms() {
        return programDao.getBuiltInPrograms();
    }

    public Flowable<List<ProgramInfo>> getRecentPrograms(int limit) {
        return programDao.getRecentPrograms(limit);
    }

    public Maybe<Program> getProgram(int programId) {
        return programDao.getProgramById(programId);
    }

    public void addProgram(Program program) {
        programDao.insertProgram(program);
    }

    public void updateLastOpenedAt(int programId, Date lastOpenedAt) {
        programDao.updateLastOpenedAt(programId, lastOpenedAt);
    }

    public Single<Save> getSave(int saveId) {
        return saveDao.getSaveById(saveId);
    }

    public void deleteSave(int saveId) {
        saveDao.deleteSave(saveId);
    }

    public Flowable<List<SaveInfo>> getSavesOfProgram(int programId) {
        return saveDao.getSavesByProgramId(programId);
    }
}
