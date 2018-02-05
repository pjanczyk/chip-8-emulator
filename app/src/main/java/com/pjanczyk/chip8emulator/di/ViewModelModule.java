package com.pjanczyk.chip8emulator.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.pjanczyk.chip8emulator.ui.emulator.EmulatorViewModel;
import com.pjanczyk.chip8emulator.ui.programs.ProgramsViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
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
}
