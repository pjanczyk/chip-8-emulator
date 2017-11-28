package com.pjanczyk.chip8emulator.vm;

public class Chip8EmulationException extends Exception {

    public enum Type {
        STACK_OVERFLOW,
        STACK_UNDERFLOW,
        INVALID_INSTRUCTION,
        PROGRAM_COUNTER_OUT_OF_RANGE
    }

    private Type type;
    private String message;
    private int instruction;
    private int programCounter;

    public Chip8EmulationException(Type type, String message, int instruction, int programCounter) {
        this.type = type;
        this.message = message;
        this.instruction = instruction;
        this.programCounter = programCounter;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getInstruction() {
        return instruction;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    @Override
    public String toString() {
        if (message == null) {
            return type.toString();
        } else {
            return type.toString() + ": " + message;
        }
    }
}
