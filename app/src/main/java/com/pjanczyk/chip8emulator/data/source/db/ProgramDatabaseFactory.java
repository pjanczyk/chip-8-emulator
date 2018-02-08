package com.pjanczyk.chip8emulator.data.source.db;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;

import com.pjanczyk.chip8emulator.data.source.xml.BuiltInProgramsProvider;
import com.pjanczyk.chip8emulator.data.Program;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class ProgramDatabaseFactory {
    private final Application application;
    private final BuiltInProgramsProvider builtInProgramsProvider;

    @Inject
    public ProgramDatabaseFactory(Application application,
                                  BuiltInProgramsProvider builtInProgramsProvider) {
        this.application = application;
        this.builtInProgramsProvider = builtInProgramsProvider;
    }

    public ProgramDatabase create() {
        ProgramDatabase[] database = new ProgramDatabase[1]; // mutable reference
        database[0] = Room.databaseBuilder(application, ProgramDatabase.class, "programs.db")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<Program> builtInPrograms =
                                    builtInProgramsProvider.getBuiltInPrograms();
                            database[0].programDao().insertPrograms(builtInPrograms);
                        });
                    }
                })
                .build();
        return database[0];
    }
}
