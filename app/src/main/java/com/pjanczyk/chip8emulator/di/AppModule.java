package com.pjanczyk.chip8emulator.di;

import com.pjanczyk.chip8emulator.model.db.ProgramDao;
import com.pjanczyk.chip8emulator.model.db.ProgramDatabase;
import com.pjanczyk.chip8emulator.model.db.ProgramDatabaseFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton @Provides
    ProgramDao provideProgramDao(ProgramDatabase database) {
        return database.programDao();
    }

    @Singleton @Provides
    ProgramDatabase provideProgramDatabase(ProgramDatabaseFactory factory) {
        return factory.create();
    }
}
