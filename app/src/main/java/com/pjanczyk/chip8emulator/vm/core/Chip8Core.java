package com.pjanczyk.chip8emulator.vm.core;

import android.util.Log;

import com.pjanczyk.chip8emulator.vm.Chip8Display;
import com.pjanczyk.chip8emulator.vm.Chip8Error;
import com.pjanczyk.chip8emulator.vm.Chip8Keyboard;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

public class Chip8Core {

    private static final byte[] DEFAULT_SPRITES = {
            (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, //0
            (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, //1
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, //2
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, //3
            (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, //4
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, //5
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, //6
            (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, //7
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, //8
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, //9
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, //A
            (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, //B
            (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, //C
            (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, //D
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, //E
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80  //F
    };
    private static final Random RANDOM_GENERATOR = new Random();

    int PC = 0; // program counter
    byte[] V = new byte[16]; // registers
    int I;
    byte delayTimer;
    byte soundTimer;

    // memory
    byte[] memory = new byte[4096];

    // stack
    Deque<Integer> stack;
    Chip8DisplayImpl display;
    Chip8KeyboardImpl keyboard;

    Chip8Error lastError = null;

    public Chip8Core() {
        this(new Chip8DisplayImpl(), new Chip8KeyboardImpl());
    }

    Chip8Core(Chip8DisplayImpl display, Chip8KeyboardImpl keyboard) {
        this.display = display;
        this.keyboard = keyboard;
        loadDefaults();
    }

    public Chip8Display getDisplay() {
        return display;
    }

    public Chip8Keyboard getKeyboard() {
        return keyboard;
    }

    public void loadProgram(byte[] program) {
        if (program.length > 3584) {
            throw new IllegalArgumentException("Program cannot longer than 3584 bytes");
        }

        System.arraycopy(program, 0, memory, 512, program.length);
    }

    public void decreaseTimers() {
        if (delayTimer != 0) {
            delayTimer--;
        }
        if (soundTimer != 0) {
            soundTimer--;
        }
    }

    public Chip8Error executeNextInstruction() {
        int instruction = ((memory[PC] & 0xFF) << 8) | (memory[PC + 1] & 0xFF);
        Log.d("C8-VM", "Line " + ((PC - 0x200) / 2) + ", instr :" + Integer.toHexString(instruction));
        PC += 2;

        if (!executeInstruction(instruction)) {
            lastError = new Chip8Error(
                    Chip8Error.Type.INVALID_INSTRUCTION,
                    null,
                    instruction,
                    PC);
        }

        if (lastError != null) {
            Chip8Error temp = lastError;
            lastError = null;
            return temp;
        }
        return null;
    }

    public void loadDefaults() {
        Arrays.fill(V, (byte) 0);
        I = 0;
        delayTimer = 0;
        soundTimer = 0;
        stack = new ArrayDeque<>(24);

        System.arraycopy(DEFAULT_SPRITES, 0, memory, 0, DEFAULT_SPRITES.length);
        Arrays.fill(memory, 0x200, 0x1000, (byte) 0);
        PC = 0x200;

        display.clear();
    }

    // @formatter:off
    boolean executeInstruction(int instr) {
        switch (instr & 0xF000) {
            case 0x0000:
                switch (instr) {
                    case 0x00E0: op_00E0_CLS(instr); return true;
                    case 0x00EE: op_00EE_RET(instr); return true;
                    default: return false;
                }
            case 0x1000: op_1nnn_JP(instr); return true;
            case 0x2000: op_2nnn_CALL(instr); return true;
            case 0x3000: op_3xnn_SE(instr); return true;
            case 0x4000: op_4xnn_SNE(instr); return true;
            case 0x5000:
                switch (instr & 0x000F) {
                    case 0x0: op_5xy0_SE(instr); return true;
                    default: return false;
                }
            case 0x6000: op_6xnn_LD(instr); return true;
            case 0x7000: op_7xnn_ADD(instr); return true;
            case 0x8000:
                switch (instr & 0x000F) {
                    case 0x0: op_8xy0_LD(instr); return true;
                    case 0x1: op_8xy1_OR(instr); return true;
                    case 0x2: op_8xy2_AND(instr); return true;
                    case 0x3: op_8xy3_XOR(instr); return true;
                    case 0x4: op_8xy4_ADD(instr); return true;
                    case 0x5: op_8xy5_SUB(instr); return true;
                    case 0x6: op_8xy6_SHR(instr); return true;
                    case 0x7: op_8xy7_SUBN(instr); return true;
                    case 0xE: op_8xyE_SHL(instr); return true;
                    default: return false;
                }
            case 0x9000:
                switch (instr & 0x000F) {
                    case 0x0: op_9xy0_SNE(instr); return true;
                    default: return false;
                }
            case 0xA000: op_Annn_LD(instr); return true;
            case 0xB000: op_Bnnn_JP(instr); return true;
            case 0xC000: op_Cxnn_RND(instr); return true;
            case 0xD000: op_Dxyn_DRW(instr); return true;
            case 0xE000:
                switch (instr & 0x00FF) {
                    case 0x9E: op_Ex9E_SKP(instr); return true;
                    case 0xA1: op_ExA1_SKNP(instr); return true;
                    default: return false;
                }
            case 0xF000:
                switch (instr & 0x00FF) {
                    case 0x07: op_Fx07_LD(instr); return true;
                    case 0x0A: op_Fx0A_LD(instr); return true;
                    case 0x15: op_Fx15_LD(instr); return true;
                    case 0x18: op_Fx18_ST(instr); return true;
                    case 0x1E: op_Fx1E_ADD(instr); return true;
                    case 0x29: op_Fx29_LD(instr); return true;
                    case 0x33: op_Fx33_LD(instr); return true;
                    case 0x55: op_Fx55_LD(instr); return true;
                    case 0x65: op_Fx65_LD(instr); return true;
                    default: return false;
                }
            default: return false;
        }
    }
    // @formatter:on

    private void op_00E0_CLS(int instr) {
        display.clear();
    }

    private void op_00EE_RET(int instr) {
        if (stack.isEmpty()) {
            lastError = new Chip8Error(
                    Chip8Error.Type.STACK_UNDERFLOW,
                    "Stack underflow error (RET instruction)", instr, PC);
            return;
        }

        PC = stack.pop();
    }

    private void op_1nnn_JP(int instr) {
        PC = instr & 0x0FFF;
    }

    private void op_2nnn_CALL(int instr) {
        if (stack.size() == 24) {
            lastError = new Chip8Error(
                    Chip8Error.Type.STACK_OVERFLOW,
                    "Stack overflow error (CALL instruction)", instr, PC);
            return;
        }

        stack.push(PC);
        PC = instr & 0x0FFF;
    }

    private void op_3xnn_SE(int instr) {
        int reg = (instr >>> 8) & 0xF;
        byte value = (byte) (instr & 0xFF);

        if (V[reg] == value) {
            PC += 2;
        }
    }

    private void op_4xnn_SNE(int instr) {
        int reg = (instr >>> 8) & 0xF;
        byte value = (byte) (instr & 0xFF);

        if (V[reg] != value) {
            PC += 2;
        }
    }

    private void op_5xy0_SE(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;
        if (V[reg1] == V[reg2]) {
            PC += 2;
        }
    }

    private void op_6xnn_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        byte value = (byte) (instr & 0xFF);

        V[reg] = value;
    }

    private void op_7xnn_ADD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        byte value = (byte) (instr & 0xFF);

        V[reg] += value;
    }

    private void op_8xy0_LD(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        V[reg1] = V[reg2];
    }

    private void op_8xy1_OR(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        V[reg1] |= V[reg2];
    }

    private void op_8xy2_AND(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        V[reg1] &= V[reg2];
    }

    private void op_8xy3_XOR(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        V[reg1] ^= V[reg2];
    }

    private void op_8xy4_ADD(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        int sum = (V[reg1] & 0xFF) + (V[reg2] & 0xFF);

        V[0xF] = (byte) (sum >>> 8); //carry flag - 0 or 1
        V[reg1] = (byte) (sum & 0xFF);
    }

    private void op_8xy5_SUB(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        int diff = (V[reg1] & 0xFF) - (V[reg2] & 0xFF);

        V[0xF] = (byte) (diff < 0 ? 0 : 1); // not borrow
        V[reg1] = (byte) (diff & 0xFF);
    }

    private void op_8xy6_SHR(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        V[0xF] = (byte) (V[reg1] & 1);
        V[reg1] = (byte) ((V[reg1] & 0xFF) >>> 1);
    }

    private void op_8xy7_SUBN(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        int diff = (V[reg2] & 0xFF) - (V[reg1] & 0xFF);

        V[0xF] = (byte) (diff < 0 ? 0 : 1); // not borrow
        V[reg1] = (byte) (diff & 0xFF);
    }

    private void op_8xyE_SHL(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        V[0xF] = (byte) (V[reg1] < 0 ? 1 : 0);
        V[reg1] <<= 1;
    }

    private void op_9xy0_SNE(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;

        if (V[reg1] != V[reg2]) {
            PC += 2;
        }
    }

    private void op_Annn_LD(int instr) {
        I = instr & 0x0FFF;
    }

    private void op_Bnnn_JP(int instr) {
        PC = (instr & 0x0FFF) + (V[0] & 0xFF);
    }

    private void op_Cxnn_RND(int instr) {
        int reg = (instr >>> 8) & 0xF;
        byte value = (byte) (instr & 0xFF);

        V[reg] = (byte) (RANDOM_GENERATOR.nextInt() & value);
    }

    private void op_Dxyn_DRW(int instr) {
        int reg1 = (instr >>> 8) & 0xF;
        int reg2 = (instr >>> 4) & 0xF;
        int length = instr & 0xF;

        int x = V[reg1] & 0xFF;
        int y = V[reg2] & 0xFF;

        boolean anyErased = display.drawSprite(x, y, memory, I, length);

        V[0xF] = anyErased ? (byte) 1 : (byte) 0;
    }

    private void op_Ex9E_SKP(int instr) {
        int reg = (instr >>> 8) & 0xF;

        int key = V[reg] & 0xFF;

        if (keyboard.isKeyPressed(key)) {
            PC += 2;
        }
    }

    private void op_ExA1_SKNP(int instr) {
        int reg = (instr >>> 8) & 0xF;

        int key = V[reg] & 0xFF;

        if (!keyboard.isKeyPressed(key)) {
            PC += 2;
        }
    }

    private void op_Fx07_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        V[reg] = delayTimer;
    }

    private void op_Fx0A_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        if (!keyboard.isAnyKeyPressed()) {
            PC -= 2; //repeat this instruction
        } else {
            for (byte key = 0; key < 16; key++) {
                if (keyboard.isKeyPressed(key)) {
                    V[reg] = key;
                }
            }
        }
    }

    private void op_Fx15_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        delayTimer = V[reg];
    }

    private void op_Fx18_ST(int instr) {
        int reg = (instr >>> 8) & 0xF;
        soundTimer = V[reg];
    }

    private void op_Fx1E_ADD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        I += V[reg] & 0xFF;
        I &= 0xFFF;
    }

    private void op_Fx29_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        I = (V[reg] & 0xFF) * 5;
    }

    private void op_Fx33_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;
        int number = V[reg] & 0xFF;

        memory[I] = (byte) (number / 100);
        memory[I + 1] = (byte) ((number / 10) % 10);
        memory[I + 2] = (byte) (number % 10);
    }

    private void op_Fx55_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;

        System.arraycopy(V, 0, memory, I, reg + 1);
    }

    private void op_Fx65_LD(int instr) {
        int reg = (instr >>> 8) & 0xF;

        System.arraycopy(memory, I, V, 0, reg + 1);
    }

}
