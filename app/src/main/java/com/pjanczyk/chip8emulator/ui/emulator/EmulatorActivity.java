package com.pjanczyk.chip8emulator.ui.emulator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.vm.Chip8EmulationException;
import com.pjanczyk.chip8emulator.vm.Chip8ReadOnlyDisplay;
import com.pjanczyk.chip8emulator.vm.Chip8VM;

public class EmulatorActivity extends AppCompatActivity {

    public static final String EXTRA_PROGRAM = "program";

    private DisplayView displayView;
    private KeyboardView keyboardView;
    private AppBarLayout appBar;
    private Toolbar toolbar;

    private Handler mainThreadHandler;

    private Program program;
    private byte[] programBytecode;

    private boolean paused;

    private Chip8VM vm;

    private final Chip8VM.Listener vmListener = new Chip8VM.Listener() {
        @Override
        public void onDisplayRedraw(Chip8ReadOnlyDisplay display) {
            displayView.requestRender();
        }

        @Override
        public void onError(Chip8EmulationException ex) {
            mainThreadHandler.post(() -> {
                Toast.makeText(EmulatorActivity.this,
                        "Error: " + ex.toString(), Toast.LENGTH_LONG).show();
                EmulatorActivity.this.finish();
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emulator);
        appBar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        displayView = findViewById(R.id.display);
        keyboardView = findViewById(R.id.keyboard);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        program = intent.getParcelableExtra(EXTRA_PROGRAM);
        if (program == null) {
            throw new NullPointerException();
        }

        programBytecode = program.readBytecode(this);

        mainThreadHandler = new Handler(getMainLooper());

        paused = true;
        vm = new Chip8VM();

//        vm.setClockPeriods(1_000_000_000 / 20, 1_000_000_000 / 2);

        vm.setListener(vmListener);
        vm.loadProgram(programBytecode);
        vm.start();

        displayView.setDisplay(vm.getDisplay());
        displayView.setOnClickListener(view -> {
            toggle();
        });

        keyboardView.setKeyListener((key, pressed) -> {
            vm.getKeyboard().setKeyPressed(key, pressed);
        });

        setTitle(program.getDisplayName());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        resumeVM();
        mainThreadHandler.postDelayed(this::pauseVM, 150);
    }

    @Override
    protected void onDestroy() {
        vm.stop();
        super.onDestroy();
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
        mainThreadHandler.removeCallbacks(this::pauseVM);
        if (paused) {
            resumeVM();
        } else {
            pauseVM();
        }
    }

    @SuppressLint("InlinedApi")
    private void pauseVM() {
        paused = true;
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
    private void resumeVM() {
        paused = false;

        // Show the system bar
        displayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        appBar.setVisibility(View.VISIBLE);
    }

}
