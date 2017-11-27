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

    private final Object stateLock = new Object();
    private final Object listenerLock = new Object();

    private final ScheduledExecutorService scheduler;
    private final Chip8Core core;

    private int instructionClockPeriod = DEFAULT_INSTRUCTION_CLOCK_INTERVAL;
    private int timerClockPeriod = DEFAULT_TIMER_CLOCK_INTERVAL;

    private boolean isRunning;
    private Listener listener;

    private ScheduledFuture instructionClockFuture;
    private ScheduledFuture timerClockFuture;

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
        synchronized (listenerLock) {
            this.listener = listener;
        }
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

    public void start() {
        synchronized (stateLock) {
            assertStopped();
            isRunning = true;

            instructionClockFuture = scheduler.scheduleAtFixedRate(this::onInstructionClockTick,
                    0, instructionClockPeriod, TimeUnit.NANOSECONDS);

            timerClockFuture = scheduler.scheduleAtFixedRate(this::onTimerClockTick,
                    timerClockPeriod, timerClockPeriod, TimeUnit.NANOSECONDS);
        }
    }

    public void stop() {
        synchronized (stateLock) {
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

    private void assertStopped() {
        if (isRunning) {
            throw new IllegalStateException("VM is running");
        }
    }

    private void onInstructionClockTick() {
        final Chip8Error error = core.executeNextInstruction();

        if (error != null) {
            Log.d(TAG, error.toString());

            new Thread(() -> {
                Chip8VM.this.stop();
                synchronized (listenerLock) {
                    if (listener != null) {
                        listener.onError(error);
                    }
                }
            }).start();
        } else {
            synchronized (listenerLock) {
                if (listener != null) {
                    listener.onDisplayRedraw(getDisplay());
                }
            }
        }
    }

    private void onTimerClockTick() {
        core.decreaseTimers();
    }

    public interface Listener {
        void onDisplayRedraw(Chip8ReadOnlyDisplay display);

        void onError(Chip8Error error);
    }
}
