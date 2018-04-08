package com.pjanczyk.chip8emulator.vm;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Chip8VM {

    public static final int DEFAULT_INSTRUCTION_CLOCK_INTERVAL = 1_000_000_000 / 500; // 500 Hz
    public static final int DEFAULT_TIMER_CLOCK_INTERVAL = 1_000_000_000 / 60; // 60 Hz

    private static final String TAG = "Chip8VM";

    private volatile Listener listener;

    private volatile int instructionClockPeriod = DEFAULT_INSTRUCTION_CLOCK_INTERVAL;
    private volatile int timerClockPeriod = DEFAULT_TIMER_CLOCK_INTERVAL;

    private final Object stateLock = new Object();
    private volatile ScheduledExecutorService executor;

    private final Chip8Core core;

    private volatile boolean isPlayingTone;

    public Chip8VM() {
        core = new Chip8Core(new Chip8Display(), new Chip8Keyboard());
    }

    public Chip8ReadOnlyDisplay getDisplay() {
        return core.getDisplay();
    }

    public Chip8KeyboardInput getKeyboard() {
        return core.getKeyboard();
    }

    public boolean isRunning() {
        return executor != null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setClockPeriods(int instructionClockPeriod, int timerClockPeriod) {
        synchronized (stateLock) {
            assertStopped();
            this.instructionClockPeriod = instructionClockPeriod;
            this.timerClockPeriod = timerClockPeriod;
        }
    }

    public void clearMemory() {
        synchronized (stateLock) {
            assertStopped();
            core.loadDefaults();
        }
    }

    public void loadProgram(byte[] bytecode) {
        synchronized (stateLock) {
            assertStopped();
            core.loadProgram(bytecode);
        }
    }

    public Chip8State saveState() {
        synchronized (stateLock) {
            assertStopped();
            return core.saveState();
        }
    }

    public void restoreState(Chip8State state) {
        synchronized (stateLock) {
            assertStopped();
            core.restoreState(state);
        }
    }

    public void start() {
        synchronized (stateLock) {
            assertStopped();

            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this::onInstructionClockTick,
                    0, instructionClockPeriod, TimeUnit.NANOSECONDS);
            executor.scheduleAtFixedRate(this::onTimerClockTick,
                    timerClockPeriod, timerClockPeriod, TimeUnit.NANOSECONDS);

            Log.d(TAG, "Execution started");
        }
    }

    public void stop() {
        synchronized (stateLock) {
            if (isRunning()) {
                executor.shutdown();

                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                executor = null;

                Log.d(TAG, "Execution stopped");
            }
        }
    }

    private void onInstructionClockTick() {
        Log.v(TAG, "onInstructionClockTick");

        try {
            core.executeNextInstruction();
        } catch (Chip8EmulationException ex) {
            Log.d(TAG, "Emulation error", ex);

            executor.shutdown(); // request stop

            new Thread(() -> {
                Chip8VM.this.stop();
                Listener listener = this.listener;
                if (listener != null) {
                    listener.onError(ex);
                }
            }).start();

            return;
        }

        Listener listener = this.listener;
        if (listener != null) {
            listener.onDisplayRedraw(getDisplay());
        }
    }

    private void onTimerClockTick() {
        Log.v(TAG, "onTimerClockTick");

        core.decreaseTimers();

        boolean isPlayingTone = core.isPlayingTone();
        if (isPlayingTone != this.isPlayingTone) {
            this.isPlayingTone = isPlayingTone;

            Listener listener = this.listener;
            if (listener != null) {
                listener.onIsPlayingToneChanged(isPlayingTone);
            }
        }
    }

    private void assertStopped() {
        if (isRunning()) {
            throw new IllegalStateException("VM is running");
        }
    }

    public interface Listener {
        void onDisplayRedraw(Chip8ReadOnlyDisplay display);

        void onError(Chip8EmulationException ex);

        void onIsPlayingToneChanged(boolean isPlayingTone);
    }
}
