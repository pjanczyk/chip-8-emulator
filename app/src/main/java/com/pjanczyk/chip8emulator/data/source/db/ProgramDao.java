package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pjanczyk.chip8emulator.data.Program;
import com.pjanczyk.chip8emulator.data.ProgramInfo;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface ProgramDao {
    @Insert
    void insertProgram(Program program);

    @Insert
    void insertPrograms(List<Program> programs);

    @Update
    void updateProgram(Program program);

    @Query("SELECT * FROM Program WHERE id = :id")
    Maybe<Program> getProgramById(int id);

    @Query("SELECT id, name, isBuiltIn, author, releaseDate, description, lastOpenedAt " +
            "FROM Program " +
            "WHERE isBuiltIn = 1 " +
            "ORDER BY name")
    Flowable<List<ProgramInfo>> getBuiltInPrograms();

    @Query("SELECT id, name, isBuiltIn, author, releaseDate, description, lastOpenedAt " +
            "FROM Program " +
            "WHERE lastOpenedAt IS NOT NULL " +
            "ORDER BY lastOpenedAt DESC " +
            "LIMIT :limit")
    Flowable<List<ProgramInfo>> getRecentPrograms(int limit);

}
