package com.pjanczyk.chip8emulator.vm;

/**
 * Monochromatic virtual display (read-only)
 *
 * Its content can be read pixel-by-pixel.
 */
public interface Chip8Display {

    /**
     * Returns the width of the screen in pixels
     */
    int getWidth();

    /**
     * Returns the height of the screen in pixels
     */
    int getHeight();

    /**
     * Returns a value of a single pixel.
     *
     * @param x x-coordinate of a pixel
     * @param y y-coordinate of a pixel
     * @return {@code true} if pixel is white
     *         {@code false} if pixel is black
     */
    boolean getPixel(int x, int y);
}
