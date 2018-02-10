package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.pjanczyk.chip8emulator.data.Program;

@Database(entities = {Program.class}, version = 1)
@TypeConverters({DateConverter.class, Chip8StateConverter.class})
public abstract class ProgramDatabase extends RoomDatabase {
    public abstract ProgramDao programDao();
}
