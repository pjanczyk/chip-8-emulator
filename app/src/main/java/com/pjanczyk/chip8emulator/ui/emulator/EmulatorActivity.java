package com.pjanczyk.chip8emulator.ui.emulator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.vm.Chip8Display;
import com.pjanczyk.chip8emulator.vm.Chip8Error;
import com.pjanczyk.chip8emulator.vm.Chip8VM;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EmulatorActivity extends AppCompatActivity implements Chip8VM.Listener {

    public static final String EXTRA_PROGRAM = "program";

    private Program program;
    private byte[] programBytecode;

    private final Handler hideHandler = new Handler();
    private DisplayView displayView;
    private KeyboardView keyboardView;
    private AppBarLayout appBar;
    private Toolbar toolbar;
    private boolean visible;
    private final Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private Chip8VM vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        program = extras.getParcelable(EXTRA_PROGRAM);
        programBytecode = program.readBytecode(this);

        setContentView(R.layout.activity_emulator);

        visible = true;
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        displayView = (DisplayView) findViewById(R.id.display);
        keyboardView = (KeyboardView) findViewById(R.id.keyboard);

        vm = new Chip8VM();

        vm.setListener(this);
        vm.loadProgram(programBytecode);
        vm.start();

        displayView.setDisplay(vm.getDisplay());
        displayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        double aspectRatio =
                (double) vm.getDisplay().getHeight() / (double) vm.getDisplay().getWidth();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int displayWidth = metrics.widthPixels;
        int displayHeight = (int) (aspectRatio * metrics.widthPixels);
        displayView.setLayoutParams(new FrameLayout.LayoutParams(displayWidth, displayHeight));

        int keyboardSize = Math.min(displayWidth, metrics.heightPixels - displayHeight);
        keyboardView.setLayoutParams(new LinearLayout.LayoutParams(keyboardSize, keyboardSize));


        keyboardView.setKeyListener(new KeyboardView.KeyListener() {
            @Override
            public void onKeyStateChanged(int key, boolean pressed) {
                vm.getKeyboard().setKeyPressed(key, pressed);
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(program.getName());

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        show();
        hideHandler.postDelayed(hideRunnable, 150);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emulator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        hideHandler.removeCallbacks(hideRunnable);
        if (visible) {
            hide();
        } else {
            show();
        }
    }

    @SuppressLint("InlinedApi")
    private void hide() {
        visible = false;
        appBar.setVisibility(View.GONE);


        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        displayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        visible = true;

        // Show the system bar
        displayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        appBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDisplayRedraw(Chip8Display display) {
        displayView.requestRender();
    }

    @Override
    public void onError(Chip8Error error) {
        throw new UnsupportedOperationException(error.toString());
    }
}
