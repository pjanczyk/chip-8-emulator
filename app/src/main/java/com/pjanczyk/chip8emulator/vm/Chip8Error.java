package com.pjanczyk.chip8emulator.vm;

import android.os.Parcel;
import android.os.Parcelable;

public class Chip8Error implements Parcelable {

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

    public Chip8Error(Type type, String message, int instruction, int programCounter) {
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

    private Chip8Error(Parcel in) {
        message = in.readString();
        instruction = in.readInt();
        programCounter = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeInt(instruction);
        dest.writeInt(programCounter);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Chip8Error> CREATOR = new Creator<Chip8Error>() {
        @Override
        public Chip8Error createFromParcel(Parcel in) {
            return new Chip8Error(in);
        }

        @Override
        public Chip8Error[] newArray(int size) {
            return new Chip8Error[size];
        }
    };
}
