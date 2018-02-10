package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.pjanczyk.chip8emulator.data.Save;

import io.reactivex.Maybe;

@Dao
public interface SaveDao {
    @Query("SELECT * FROM Save WHERE programId = :programId")
    Maybe<Save> getSaveByProgramId(int programId);

    @Insert
    void insertSave(Save save);
}
