package com.pjanczyk.chip8emulator.vm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Chip-8 Virtual Machine
 *
 * It is capable of executing Chip-8 bytecode, handling timers, updating virtual screen,
 * and reading virtual keyboard.
 * All the execution occurs in a special thread.
 *
 * The VM can be repeatedly started and stopped at any time.
 * When the VM is running, it is not possible to change clock periods, clear the memory,
 * or load a program.
 *
 * When an execution error occurs, the VM stops itself and than calls {@link Listener#onError}.
 *
 * The VM provides interfaces for
 * virtual display {@link Chip8Display} and virtual keyboard {@link Chip8Keyboard}
 *
 */
public class Chip8VM {

    public interface Listener {
        void onDisplayRedraw(Chip8ReadOnlyDisplay display);

        /**
         * Called when an error occurs during execution of bytecode
         * (e.g. invalid instruction opcode, buffer overflow)
         */
        void onError(Chip8Error error);
    }

    private final Object listenerLock = new Object();
    private final Object stateLock = new Object();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Chip8Core core;

    private int instructionClockPeriod = 1_000_000_000 / 500; // 500 Hz
    private int timerClockPeriod = 1_000_000_000 / 60; // 60 Hz
    private int displayRefreshPeriod = 1_000_000_000 / 60; // 60 Hz

    private boolean isRunning;
    private Listener listener;

    public Chip8VM() {
        core = new Chip8Core(new Chip8Display(), new Chip8Keyboard());
    }

    public Chip8ReadOnlyDisplay getDisplay() {
        return core.getDisplay();
    }

    public Chip8Keyboard getKeyboard() {
        return core.getKeyboard();
    }

    public void setListener(Listener listener) {
        synchronized (listenerLock) {
            this.listener = listener;
        }
    }

    /**
     * Checks if the VM is running.
     */
    public boolean isRunning() {
        synchronized (stateLock) {
            return isRunning;
        }
    }

    /**
     * Sets a period of executing instructions and period of clock of timers.
     * VM must be stopped to perform this operation.
     *
     * @throws IllegalStateException if VM is running
     */
    public void setClockPeriods(int instructionClockPeriod, int timerClockPeriod) {
        synchronized (stateLock) {
            assertStopped();
            this.instructionClockPeriod = instructionClockPeriod;
            this.timerClockPeriod = timerClockPeriod;
        }
    }

    /**
     * Loads {@param bytecode} of a program into the memory.
     * VM must be stopped to perform this operation.
     *
     * @throws IllegalStateException if VM is running
     */
    public void loadProgram(byte[] bytecode) {
        synchronized (stateLock) {
            assertStopped();
            core.loadProgram(bytecode);
        }
    }

    /**
     * Clears all registers, stack, and memory.
     * VM must be stopped to perform this operation.
     *
     * @throws IllegalStateException if VM is running
     */
    public void clearMemory() {
        synchronized (stateLock) {
            assertStopped();
            core.loadDefaults();
        }
    }

    /**
     * Starts the VM. Bytecode is executed in a background thread.
     *
     * @throws IllegalStateException if VM is already running
     */
    public void start() {
        synchronized (stateLock) {
            if (isRunning) {
                throw new IllegalStateException("VM is already started");
            }

            isRunning = true;

            scheduler.scheduleAtFixedRate(this::onInstructionClockTick,
                    0, instructionClockPeriod, TimeUnit.NANOSECONDS);

            scheduler.scheduleAtFixedRate(this::onTimerClockTick,
                    timerClockPeriod, timerClockPeriod, TimeUnit.NANOSECONDS);

            scheduler.scheduleAtFixedRate(this::onDisplayRefreshClockTick,
                    displayRefreshPeriod, displayRefreshPeriod, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Stops execution of bytecode and delay and sound timers.
     * Waits until completion of execution of a current instruction.
     * Does nothing if VM is already stopped.
     */
    public void stop() {
        synchronized (stateLock) {
            if (isRunning) {
                isRunning = false;

                scheduler.shutdown();
                try {
                    if (scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Scheduler cannot be terminated");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void assertStopped() {
        if (isRunning) {
            throw new IllegalStateException("VM must be stopped to perform this operation");
        }
    }

    private void onInstructionClockTick() {
        final Chip8Error error = core.executeNextInstruction();

        if (error != null) {
            new Thread(() -> {
                Chip8VM.this.stop();
                synchronized (listenerLock) {
                    if (listener != null) {
                        listener.onError(error);
                    }
                }
            }).start();
        }

        synchronized (listenerLock) {
            if (listener != null) {
                listener.onDisplayRedraw(core.getDisplay());
            }
        }
    }

    private void onTimerClockTick() {
        core.decreaseTimers();
    }

    private void onDisplayRefreshClockTick() {

    }
}
