package com.pjanczyk.chip8emulator.ui.emulator;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.pjanczyk.chip8emulator.vm.Chip8Display;

import java.util.Timer;
import java.util.TimerTask;

public class DisplayView extends GLSurfaceView {

    private final DisplayRenderer renderer;

    public DisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        renderer = new DisplayRenderer();

        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        /*Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                requestRender();
            }
        }, 0, 17);*/
    }

    public DisplayView(Context context) {
        this(context, null);
    }

    public void setDisplay(Chip8Display display) {
        renderer.setDisplay(display);
        requestRender();
    }
}
