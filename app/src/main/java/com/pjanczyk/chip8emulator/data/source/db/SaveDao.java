package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.pjanczyk.chip8emulator.data.Save;
import com.pjanczyk.chip8emulator.data.SaveInfo;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface SaveDao {
    @Query("SELECT * FROM Save WHERE id = :id")
    Single<Save> getSaveById(int id);

    @Query("SELECT id, programId, createdAt " +
            "FROM Save " +
            "WHERE programId = :programId " +
            "ORDER BY createdAt")
    Flowable<List<SaveInfo>> getSavesByProgramId(int programId);

    @Insert
    void insertSave(Save save);

    @Query("DELETE FROM Save WHERE id = :id")
    void deleteSave(int id);
}
