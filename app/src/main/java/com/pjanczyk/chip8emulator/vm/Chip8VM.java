package com.pjanczyk.chip8emulator.vm;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Chip8VM {

    public static final int DEFAULT_INSTRUCTION_CLOCK_INTERVAL = 1_000_000_000 / 500; // 500 Hz
    public static final int DEFAULT_TIMER_CLOCK_INTERVAL = 1_000_000_000 / 60; // 60 Hz

    private static final String TAG = "Chip8VM";

    private final Object isRunningLock = new Object();

    private final ScheduledExecutorService scheduler;
    private final Chip8Core core;

    private volatile Listener listener;
    private volatile boolean isRunning;

    private volatile int instructionClockPeriod = DEFAULT_INSTRUCTION_CLOCK_INTERVAL;
    private volatile int timerClockPeriod = DEFAULT_TIMER_CLOCK_INTERVAL;

    private volatile ScheduledFuture instructionClockFuture;
    private volatile ScheduledFuture timerClockFuture;

    public Chip8VM() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        core = new Chip8Core(new Chip8Display(), new Chip8Keyboard());
    }

    public Chip8ReadOnlyDisplay getDisplay() {
        return core.getDisplay();
    }

    public Chip8Keyboard getKeyboard() {
        return core.getKeyboard();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setClockPeriods(int instructionClockPeriod, int timerClockPeriod) {
        synchronized (isRunningLock) {
            assertStopped();
            this.instructionClockPeriod = instructionClockPeriod;
            this.timerClockPeriod = timerClockPeriod;
        }
    }

    public void clearMemory() {
        synchronized (isRunningLock) {
            assertStopped();
            core.loadDefaults();
        }
    }

    public void loadProgram(byte[] bytecode) {
        synchronized (isRunningLock) {
            assertStopped();
            core.loadProgram(bytecode);
        }
    }

    public void start() {
        synchronized (isRunningLock) {
            assertStopped();
            isRunning = true;

            instructionClockFuture = scheduler.scheduleAtFixedRate(this::onInstructionClockTick,
                    0, instructionClockPeriod, TimeUnit.NANOSECONDS);

            timerClockFuture = scheduler.scheduleAtFixedRate(this::onTimerClockTick,
                    timerClockPeriod, timerClockPeriod, TimeUnit.NANOSECONDS);
        }
    }

    public void stop() {
        synchronized (isRunningLock) {
            if (isRunning) {
                isRunning = false;

                instructionClockFuture.cancel(false);
                timerClockFuture.cancel(false);

                try {
                    scheduler.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void onInstructionClockTick() {
        final Chip8Error error = core.executeNextInstruction();

        if (error != null) {
            Log.d(TAG, error.toString());

            new Thread(() -> {
                Chip8VM.this.stop();
                Listener listener = this.listener;
                if (listener != null) {
                    listener.onError(error);
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
        core.decreaseTimers();
    }

    private void assertStopped() {
        if (isRunning) {
            throw new IllegalStateException("VM is running");
        }
    }

    public interface Listener {
        void onDisplayRedraw(Chip8ReadOnlyDisplay display);

        void onError(Chip8Error error);
    }
}
