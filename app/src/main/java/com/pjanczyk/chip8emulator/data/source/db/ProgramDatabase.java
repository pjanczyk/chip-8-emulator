package com.pjanczyk.chip8emulator.data.source.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.pjanczyk.chip8emulator.data.Program;
import com.pjanczyk.chip8emulator.data.Save;

@Database(entities = {Program.class, Save.class}, version = 1)
@TypeConverters({DateConverter.class, ImmutableArrayConverter.class})
public abstract class ProgramDatabase extends RoomDatabase {
    public abstract ProgramDao programDao();
    public abstract SaveDao saveDao();
}
