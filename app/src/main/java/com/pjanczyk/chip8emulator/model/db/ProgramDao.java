package com.pjanczyk.chip8emulator.model.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.ProgramInfo;

import java.util.Date;
import java.util.List;

@Dao
public interface ProgramDao {
    @Insert
    void insertProgram(Program program);

    @Insert
    void insertPrograms(List<Program> programs);

    @Update
    void updateProgram(Program program);

    @Query("SELECT * FROM Program WHERE id = :id")
    LiveData<Program> getProgramById(int id);

    @Query("UPDATE Program " +
            "SET lastOpenedAt = :lastOpenedAt " +
            "WHERE id = :id")
    void updateLastOpenedAt(int id, Date lastOpenedAt);

    @Query("SELECT id, name, isBuiltIn, author, releaseDate, description, lastOpenedAt " +
            "FROM Program " +
            "WHERE isBuiltIn = 1")
    LiveData<List<ProgramInfo>> getBuiltInPrograms();

    @Query("SELECT id, name, isBuiltIn, author, releaseDate, description, lastOpenedAt " +
            "FROM Program " +
            "WHERE lastOpenedAt IS NOT NULL " +
            "ORDER BY lastOpenedAt DESC " +
            "LIMIT :limit")
    LiveData<List<ProgramInfo>> getRecentPrograms(int limit);
}
