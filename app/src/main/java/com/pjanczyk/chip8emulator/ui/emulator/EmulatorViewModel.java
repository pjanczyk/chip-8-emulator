package com.pjanczyk.chip8emulator.ui.emulator;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;

import com.pjanczyk.chip8emulator.data.Program;
import com.pjanczyk.chip8emulator.data.source.KeyBinding;
import com.pjanczyk.chip8emulator.data.source.ProgramRepository;
import com.pjanczyk.chip8emulator.vm.Chip8EmulationException;
import com.pjanczyk.chip8emulator.vm.Chip8KeyboardInput;
import com.pjanczyk.chip8emulator.vm.Chip8ReadOnlyDisplay;
import com.pjanczyk.chip8emulator.vm.Chip8State;
import com.pjanczyk.chip8emulator.vm.Chip8VM;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class EmulatorViewModel extends ViewModel {
    private final ProgramRepository repository;
    private final Chip8VM vm = new Chip8VM();

    private final MutableLiveData<Program> program = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRunning = new MutableLiveData<>();
    private final MutableLiveData<Chip8ReadOnlyDisplay> display = new MutableLiveData<>();

    private final PublishSubject<Chip8EmulationException> emulationError = PublishSubject.create();
    private State state = State.INITIAL;

    @Inject
    public EmulatorViewModel(ProgramRepository repository) {
        this.repository = repository;
        this.isRunning.setValue(false);
        this.vm.setListener(new VMListener());
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

    public LiveData<KeyBinding> getKeyBinding() {
        return Transformations.map(program,
                program -> program != null ? program.keyBinding : null);
    }

    public Observable<Chip8EmulationException> getEmulationError() {
        return emulationError;
    }

    @MainThread
    public void init(int programId) {
        if (state != State.INITIAL) return;
        state = State.FETCHING_PROGRAM;

        repository.getProgram(programId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    throw new RuntimeException("Program with the given id does not exist");
                })
                .subscribe(this::onProgramFetched);
    }

    @MainThread
    private void onProgramFetched(Program program) {
        vm.loadProgram(program.bytecode);
        this.program.setValue(program);

        state = State.VM_STOPPED;
    }

    @MainThread
    public void resume() {
        if (state == State.VM_STOPPED) {
            isRunning.setValue(true);
            vm.start();
            state = State.VM_STARTED;
        }
    }

    @MainThread
    public void pause() {
        if (state == State.VM_STARTED) {
            isRunning.setValue(false);
            vm.stop();
            state = State.VM_STOPPED;
        }
    }

    @MainThread
    public void toggle() {
        if (state == State.VM_STARTED) {
            pause();
        } else if (state == State.VM_STOPPED) {
            resume();
        }
    }

    @MainThread
    public void restart() {
        if (state != State.VM_STARTED && state != State.VM_STOPPED) return;

        withPausedVM(() -> {
            vm.clearMemory();
            //noinspection ConstantConditions
            vm.loadProgram(program.getValue().bytecode);
            return null;
        });

        resume();
    }

    @MainThread
    public void quickSave() {
        if (state != State.VM_STARTED && state != State.VM_STOPPED) return;

        Chip8State quickSave = withPausedVM(vm::saveState);

        //noinspection ConstantConditions
        Program updatedProgram = program.getValue().copy()
                .setQuickSave(quickSave)
                .build();

        program.setValue(updatedProgram);
        repository.updateProgram(updatedProgram)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @MainThread
    public void quickRestore() {
        if (state != State.VM_STARTED && state != State.VM_STOPPED) return;

        //noinspection ConstantConditions
        Chip8State quickSave = program.getValue().quickSave;

        if (quickSave == null) return;

        withPausedVM(() -> {
            vm.restoreState(quickSave);
            return null;
        });

        resume();
    }

    @MainThread
    public void options() {
        // TODO
    }

    @MainThread
    private <T> T withPausedVM(Callable<T> callable) {
        try {
            T result;
            if (vm.isRunning()) {
                vm.stop();
                result = callable.call();
                vm.start();
            } else {
                result = callable.call();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private enum State {
        INITIAL, // initial state after construction
        FETCHING_PROGRAM, // program has been requested from the repository
        VM_STOPPED, // program loaded into VM, VM is stopped
        VM_STARTED  // program loaded into VM, VM is running
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
