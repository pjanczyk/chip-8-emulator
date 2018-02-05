package com.pjanczyk.chip8emulator.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.pjanczyk.chip8emulator.model.Program;

@Database(entities = {Program.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class ProgramDatabase extends RoomDatabase {
    public abstract ProgramDao programDao();
}
