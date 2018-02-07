package com.pjanczyk.chip8emulator.ui.emulator;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;

import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.repository.ProgramRepository;
import com.pjanczyk.chip8emulator.vm.Chip8EmulationException;
import com.pjanczyk.chip8emulator.vm.Chip8KeyboardInput;
import com.pjanczyk.chip8emulator.vm.Chip8ReadOnlyDisplay;
import com.pjanczyk.chip8emulator.vm.Chip8VM;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class EmulatorViewModel extends ViewModel {
    private final ProgramRepository repository;

    private final MutableLiveData<Program> program = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>();
    private final MutableLiveData<Chip8ReadOnlyDisplay> display = new MutableLiveData<>();

    private final PublishSubject<Chip8EmulationException> emulationError = PublishSubject.create();

    private boolean initialized = false;
    private Chip8VM vm;

    @Inject
    public EmulatorViewModel(ProgramRepository repository) {
        this.repository = repository;
        this.isRunning.setValue(false);
    }

    public LiveData<Program> getProgram() {
        return program;
    }

    public LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    public LiveData<Chip8ReadOnlyDisplay> getDisplay() {
        return display;
    }

    public Chip8KeyboardInput getKeyboard() {
        return vm.getKeyboard();
    }

    public Observable<Chip8EmulationException> getEmulationError() {
        return emulationError;
    }

    @MainThread
    public void init(int programId) {
        if (initialized) return;
        initialized = true;

        repository.getProgram(programId)
                .subscribeOn(Schedulers.io())
                .subscribe(prog -> {
                    program.postValue(prog);
                    isRunning.postValue(true);
                    vm = new Chip8VM();
                    // vm.setClockPeriods(1_000_000_000 / 20, 1_000_000_000 / 2);

                    vm.setListener(new VMListener());
                    vm.loadProgram(prog.getBytecode());
                    vm.start();
                }, throwable -> {
                    throw new RuntimeException("Program with the given id does not exist",
                            throwable);
                });
    }

    @MainThread
    public void resume() {
        if (!isRunning.getValue()) {
            isRunning.setValue(true);
            vm.start();
        }
    }

    @MainThread
    public void pause() {
        if (isRunning.getValue()) {
            isRunning.setValue(false);
            vm.stop();
        }
    }

    @MainThread
    public void toggle() {
        if (isRunning.getValue()) {
            pause();
        } else {
            resume();
        }
    }

    private class VMListener implements Chip8VM.Listener {
        @AnyThread
        @Override
        public void onDisplayRedraw(Chip8ReadOnlyDisplay display) {
            EmulatorViewModel.this.display.postValue(display);
        }

        @AnyThread
        @Override
        public void onError(Chip8EmulationException ex) {
            emulationError.onNext(ex);
        }
    }

}