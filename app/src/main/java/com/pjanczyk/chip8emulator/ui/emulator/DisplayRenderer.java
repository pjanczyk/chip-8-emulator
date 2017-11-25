package com.pjanczyk.chip8emulator.ui.emulator;

import android.opengl.GLSurfaceView;

import com.pjanczyk.chip8emulator.vm.Chip8Display;

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

    private float color[] = {0.9f, 0.9f, 0.9f, 1.0f};

    private Chip8Display display;

    public void setDisplay(Chip8Display display) {
        this.display = display;
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
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if (display != null) {
            int width = display.getWidth();
            int height = display.getHeight();

            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(0, (float)width, (float)height, 0, -1, 1);


            gl.glMatrixMode(GL10.GL_MODELVIEW);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    boolean pixel = display.getPixel(x, y);

                    if (pixel) {
                        gl.glLoadIdentity();
                        gl.glTranslatef((float) x, (float) y, 0f);

                        // draw square
                        gl.glColor4f(color[0], color[1], color[2], color[3]);
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
}
