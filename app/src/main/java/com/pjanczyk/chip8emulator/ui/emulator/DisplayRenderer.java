package com.pjanczyk.chip8emulator.ui.emulator;

import android.opengl.GLSurfaceView;

import com.pjanczyk.chip8emulator.vm.Chip8ReadOnlyDisplay;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DisplayRenderer implements GLSurfaceView.Renderer {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private static final float SQUARE_COORDS[] = {
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f
    };

    private final short drawOrder[] = {0, 1, 2, 0, 2, 3};
    private final float color[] = {0.9f, 0.9f, 0.9f, 1.0f};

    private volatile State state;

    public void setDisplay(Chip8ReadOnlyDisplay display) {
        State oldState = this.state;

        int width = display.getWidth();
        int height = display.getHeight();

        byte[] pixels;
        if (oldState != null && oldState.pixels.length == width * height) {
            pixels = oldState.pixels.clone();
            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = (byte) Math.max(0, (pixels[i] & 0xFF) - 16);
            }
        } else {
            pixels = new byte[width * height];
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (display.getPixel(x, y)) {
                    pixels[width * y + x] = (byte) 0xFF;
                }
            }
        }

        this.state = new State(width, height, pixels);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        vertexBuffer = ByteBuffer.allocateDirect(SQUARE_COORDS.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(SQUARE_COORDS);
        vertexBuffer.position(0);

        drawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        State state = this.state;

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if (state != null) {

            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(0, (float) state.width, (float) state.height, 0, -1, 1);


            gl.glMatrixMode(GL10.GL_MODELVIEW);

            for (int y = 0; y < state.height; y++) {
                for (int x = 0; x < state.width; x++) {
                    float alpha = (state.pixels[state.width * y + x] & 0xFF) / 255.0f;

                    if (alpha > 0) {
                        gl.glLoadIdentity();
                        gl.glTranslatef((float) x, (float) y, 0f);

                        // draw square
                        gl.glColor4f(alpha * color[0],
                                alpha * color[1],
                                alpha * color[2],
                                color[3]);
                        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);

                        gl.glDrawElements(GL10.GL_TRIANGLES,
                                drawOrder.length,
                                GL10.GL_UNSIGNED_SHORT,
                                drawListBuffer);
                    }
                }
            }

        }
    }

    private static class State {
        public final int width;
        public final int height;
        public final byte[] pixels;

        public State(int width, int height, byte[] pixels) {
            this.width = width;
            this.height = height;
            this.pixels = pixels;
        }
    }
}
