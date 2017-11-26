package com.pjanczyk.chip8emulator.vm.core;

import com.pjanczyk.chip8emulator.vm.Chip8Error;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class Chip8CoreTest {

    private static final int PC0 = 0x900;

    private Chip8Core chip;
    private Chip8DisplayImpl display;
    private Chip8KeyboardImpl keyboard;

    @Before
    public void setUp() throws Exception {
        display = mock(Chip8DisplayImpl.class);
        keyboard = mock(Chip8KeyboardImpl.class);
        chip = new Chip8Core(display, keyboard);
        chip.PC = PC0;

        verify(display).clear();
        reset(display, keyboard);
    }

    @Test
    public void op_00E0_CLS() {
        chip.executeInstruction(0x00E0);

        verify(display).clear();
    }

    @Test
    public void op_00EE_RET() {
        chip.stack.push(0xABC);
        chip.executeInstruction(0x00EE);

        assertEquals(0xABC, chip.PC);
    }

    @Test
    public void op_00EE_RET_invalid() {
        chip.executeInstruction(0x00EE);

        assertEquals(Chip8Error.Type.STACK_UNDERFLOW, chip.lastError.getType());
    }

    @Test
    public void op_1nnn_JP() {
        chip.executeInstruction(0x1ABC);

        assertEquals(0xABC, chip.PC);
    }

    @Test
    public void op_2nnn_CALL() {
        chip.executeInstruction(0x2ABC);

        assertEquals(1, chip.stack.size());
        assertEquals(PC0, (int) chip.stack.pop());
        assertEquals(0xABC, chip.PC);
    }

}