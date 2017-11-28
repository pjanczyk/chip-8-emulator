package com.pjanczyk.chip8emulator.vm;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

class Chip8Core {

    private static final String TAG = "Chip8Core";

    private static final int[] DEFAULT_SPRITES = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, //0
            0x20, 0x60, 0x20, 0x20, 0x70, //1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, //2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, //3
            0x90, 0x90, 0xF0, 0x10, 0x10, //4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, //5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, //6
            0xF0, 0x10, 0x20, 0x40, 0x40, //7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, //8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, //9
            0xF0, 0x90, 0xF0, 0x90, 0x90, //A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, //B
            0xF0, 0x80, 0x80, 0x80, 0xF0, //C
            0xE0, 0x90, 0x90, 0x90, 0xE0, //D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, //E
            0xF0, 0x80, 0xF0, 0x80, 0x80  //F
    };
    private static final Random RANDOM_GENERATOR = new Random();

    int PC = 0; // 24-bit program counter
    int[] V = new int[16]; // 8-bit registers
    int I; // 24-bit register
    int[] memory = new int[4096]; // 8-bit memory

    int delayTimer;
    int soundTimer;

    // stack
    Deque<Integer> stack;
    Chip8Display display;
    Chip8Keyboard keyboard;

    Chip8Core(Chip8Display display, Chip8Keyboard keyboard) {
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
        if (program.length > 4096 - 512) {
            throw new IllegalArgumentException("Program cannot longer than 3584 bytes");
        }

        for (int i = 0; i < program.length; i++) {
            memory[512 + i] = program[i] & 0xFF;
        }
    }

    public void decreaseTimers() {
        if (delayTimer != 0) {
            delayTimer--;
        }
        if (soundTimer != 0) {
            soundTimer--;
        }
    }

    public void executeNextInstruction() throws Chip8EmulationException {
        if (PC + 1 >= memory.length) {
            throw new Chip8EmulationException(
                    Chip8EmulationException.Type.PROGRAM_COUNTER_OUT_OF_RANGE,
                    "Program counter out of range", 0, PC);
        }

        int instruction = (memory[PC] << 8) | memory[PC + 1];
        //Log.v(TAG, String.format("PC: %d, instruction: 0x%4x", PC, instruction));
        PC += 2;

        if (!executeInstruction(instruction)) {
            throw new Chip8EmulationException(
                    Chip8EmulationException.Type.INVALID_INSTRUCTION,
                    "Invalid instruction", instruction, PC);
        }
    }

    public void loadDefaults() {
        Arrays.fill(V, 0);
        I = 0;
        delayTimer = 0;
        soundTimer = 0;
        stack = new ArrayDeque<>(24);

        Arrays.fill(memory, 0);
        System.arraycopy(DEFAULT_SPRITES, 0, memory, 0, DEFAULT_SPRITES.length);

        PC = 0x200;

        display.clear();
    }

    // @formatter:off
    boolean executeInstruction(int instr) throws Chip8EmulationException {
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

    private static int arg_nnn(int instr) {
        return instr & 0x0FFF;
    }

    private static int arg_nn(int instr) {
        return instr & 0x00FF;
    }

    private static int arg_n(int instr) {
        return instr & 0x000F;
    }

    private static int arg_x(int instr) {
        return (instr & 0x0F00) >>> 8;
    }

    private static int arg_y(int instr) {
        return (instr & 0x00F0) >>> 4;
    }

    private void op_00E0_CLS(int instr) {
        display.clear();
    }

    private void op_00EE_RET(int instr) throws Chip8EmulationException {
        if (stack.isEmpty()) {
            throw new Chip8EmulationException(
                    Chip8EmulationException.Type.STACK_UNDERFLOW,
                    "Stack underflow error (RET instruction)", instr, PC);
        }

        PC = stack.pop();
    }

    private void op_1nnn_JP(int instr) {
        PC = arg_nnn(instr);
    }

    private void op_2nnn_CALL(int instr) throws Chip8EmulationException {
        if (stack.size() == 24) {
            throw new Chip8EmulationException(
                    Chip8EmulationException.Type.STACK_OVERFLOW,
                    "Stack overflow error (CALL instruction)", instr, PC);
        }

        stack.push(PC);
        PC = arg_nnn(instr);
    }

    private void op_3xnn_SE(int instr) {
        if (V[arg_x(instr)] == arg_nn(instr)) {
            PC += 2;
        }
    }

    private void op_4xnn_SNE(int instr) {
        if (V[arg_x(instr)] != arg_nn(instr)) {
            PC += 2;
        }
    }

    private void op_5xy0_SE(int instr) {
        if (V[arg_x(instr)] == V[arg_y(instr)]) {
            PC += 2;
        }
    }

    private void op_6xnn_LD(int instr) {
        V[arg_x(instr)] = arg_nn(instr);
    }

    private void op_7xnn_ADD(int instr) {
        V[arg_x(instr)] = (arg_nn(instr) + V[arg_x(instr)]) & 0xFF;
    }

    private void op_8xy0_LD(int instr) {
        V[arg_x(instr)] = V[arg_y(instr)];
    }

    private void op_8xy1_OR(int instr) {
        V[arg_x(instr)] |= V[arg_y(instr)];
    }

    private void op_8xy2_AND(int instr) {
        V[arg_x(instr)] &= V[arg_y(instr)];
    }

    private void op_8xy3_XOR(int instr) {
        V[arg_x(instr)] ^= V[arg_y(instr)];
    }

    private void op_8xy4_ADD(int instr) {
        int sum = V[arg_x(instr)] + V[arg_y(instr)];

        V[0xF] = sum > 255 ? 1 : 0; // carry
        V[arg_x(instr)] = sum & 0xFF;
    }

    private void op_8xy5_SUB(int instr) {
        int diff = V[arg_x(instr)] - V[arg_y(instr)];

        V[0xF] = diff > 0 ? 1 : 0; // not borrow
        V[arg_x(instr)] = diff & 0xFF;
    }

    private void op_8xy6_SHR(int instr) {
        V[0xF] = V[arg_x(instr)] & 1;
        V[arg_x(instr)] >>>= 1;
    }

    private void op_8xy7_SUBN(int instr) {
        int diff = V[arg_y(instr)] - V[arg_x(instr)];

        V[0xF] = diff > 0 ? 1 : 0; // not borrow
        V[arg_x(instr)] = diff & 0xFF;
    }

    private void op_8xyE_SHL(int instr) {
        V[0xF] = V[arg_x(instr)] >>> 7;
        V[arg_x(instr)] = (V[arg_x(instr)] << 1) & 0xFF;
    }

    private void op_9xy0_SNE(int instr) {
        if (V[arg_x(instr)] != V[arg_y(instr)]) {
            PC += 2;
        }
    }

    private void op_Annn_LD(int instr) {
        I = arg_nnn(instr);
    }

    private void op_Bnnn_JP(int instr) {
        PC = (arg_nnn(instr) + V[0]) & 0xFFF;
    }

    private void op_Cxnn_RND(int instr) {
        V[arg_x(instr)] = RANDOM_GENERATOR.nextInt() & arg_nn(instr);
    }

    private void op_Dxyn_DRW(int instr) {
        int startX = V[arg_x(instr)];
        int startY = V[arg_y(instr)];
        int length = arg_n(instr);

        boolean anyErased = false;

        for (int i = 0; i < length; i++) {
            int b = memory[I + i];

            for (int j = 0; j < 8; j++) {
                boolean bit = (b & (1 << (7 - j))) != 0;

                if (bit) {
                    int x = (startX + j) % display.getWidth();
                    int y = (startY + i) % display.getHeight();

                    boolean oldValue = display.getPixel(x, y);

                    if (oldValue) {
                        anyErased = true;
                    }

                    display.setPixel(x, y, !oldValue);
                }
            }
        }

        V[0xF] = anyErased ? 1 : 0;
    }

    private void op_Ex9E_SKP(int instr) {
        int key = V[arg_x(instr)];
        if (keyboard.isKeyPressed(key)) {
            PC += 2;
        }
    }

    private void op_ExA1_SKNP(int instr) {
        int key = V[arg_x(instr)];
        if (!keyboard.isKeyPressed(key)) {
            PC += 2;
        }
    }

    private void op_Fx07_LD(int instr) {
        V[arg_x(instr)] = delayTimer;
    }

    private void op_Fx0A_LD(int instr) {
        if (!keyboard.isAnyKeyPressed()) {
            PC -= 2; //repeat this instruction
            // TODO: *all* execution stops?
        } else {
            for (int key = 0; key < 16; key++) {
                if (keyboard.isKeyPressed(key)) {
                    V[arg_x(instr)] = key;
                }
            }
        }
    }

    private void op_Fx15_LD(int instr) {
        delayTimer = V[arg_x(instr)];
    }

    private void op_Fx18_ST(int instr) {
        soundTimer = V[arg_x(instr)];
    }

    private void op_Fx1E_ADD(int instr) {
        I = (I + V[arg_x(instr)]) & 0xFFF;
    }

    private void op_Fx29_LD(int instr) {
        I = 5 * V[arg_x(instr)];
    }

    private void op_Fx33_LD(int instr) {
        int number = V[arg_x(instr)];

        memory[I] = number / 100;
        memory[I + 1] = (number / 10) % 10;
        memory[I + 2] = number % 10;
    }

    private void op_Fx55_LD(int instr) {
        System.arraycopy(V, 0, memory, I, arg_x(instr) + 1);
    }

    private void op_Fx65_LD(int instr) {
        System.arraycopy(memory, I, V, 0, arg_x(instr) + 1);
    }

}
