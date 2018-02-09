package com.pjanczyk.chip8emulator.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.pjanczyk.chip8emulator.data.source.db.ProgramDao;
import com.pjanczyk.chip8emulator.data.source.db.ProgramDatabase;
import com.pjanczyk.chip8emulator.data.source.db.ProgramDatabaseFactory;
import com.pjanczyk.chip8emulator.data.source.db.SaveDao;
import com.pjanczyk.chip8emulator.ui.emulator.EmulatorActivity;
import com.pjanczyk.chip8emulator.ui.emulator.EmulatorViewModel;
import com.pjanczyk.chip8emulator.ui.programs.ProgramsActivity;
import com.pjanczyk.chip8emulator.ui.programs.ProgramsViewModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@SuppressWarnings("unused")
@Module
abstract class AppModule {
    @ContributesAndroidInjector
    abstract ProgramsActivity bindProgramsActivity();

    @ContributesAndroidInjector
    abstract EmulatorActivity bindEmulatorActivity();

    @Binds
    @IntoMap
    @ViewModelKey(ProgramsViewModel.class)
    abstract ViewModel bindProgramsViewModel(ProgramsViewModel programsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EmulatorViewModel.class)
    abstract ViewModel bindEmulatorViewModel(EmulatorViewModel emulatorViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @Singleton @Provides
    static ProgramDao provideProgramDao(ProgramDatabase database) {
        return database.programDao();
    }

    @Singleton @Provides
    static SaveDao provideSaveDao(ProgramDatabase database) {
        return database.saveDao();
    }

    @Singleton @Provides
    static ProgramDatabase provideProgramDatabase(ProgramDatabaseFactory factory) {
        return factory.create();
    }
}
