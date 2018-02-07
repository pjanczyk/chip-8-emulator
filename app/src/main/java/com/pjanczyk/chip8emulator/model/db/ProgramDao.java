package com.pjanczyk.chip8emulator.model.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.ProgramInfo;

import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ProgramDao {
    @Insert
    void insertProgram(Program program);

    @Insert
    void insertPrograms(List<Program> programs);

    @Update
    void updateProgram(Program program);

    @Query("SELECT * FROM Program WHERE id = :id")
    Single<Program> getProgramById(int id);

    @Query("UPDATE Program " +
            "SET lastOpenedAt = :lastOpenedAt " +
            "WHERE id = :id")
    void updateLastOpenedAt(int id, Date lastOpenedAt);

    @Query("SELECT id, name, isBuiltIn, author, releaseDate, description, lastOpenedAt " +
            "FROM Program " +
            "WHERE isBuiltIn = 1")
    Flowable<List<ProgramInfo>> getBuiltInPrograms();

    @Query("SELECT id, name, isBuiltIn, author, releaseDate, description, lastOpenedAt " +
            "FROM Program " +
            "WHERE lastOpenedAt IS NOT NULL " +
            "ORDER BY lastOpenedAt DESC " +
            "LIMIT :limit")
    Flowable<List<ProgramInfo>> getRecentPrograms(int limit);
}