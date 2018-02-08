package com.pjanczyk.chip8emulator.ui.emulator;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pjanczyk.chip8emulator.R;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EmulatorActivity extends AppCompatActivity {
    public static final String EXTRA_PROGRAM_ID = "program_id";

    private final LifecycleProvider<Lifecycle.Event> lifecycleProvider = AndroidLifecycle
            .createLifecycleProvider(this);

    @Inject ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.display) DisplayView displayView;
    @BindView(R.id.keyboard) KeyboardView keyboardView;
    @BindView(R.id.appbar) AppBarLayout appBar;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private EmulatorViewModel viewModel;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EmulatorViewModel.class);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int programId = intent.getIntExtra(EXTRA_PROGRAM_ID, 0);

        viewModel.getProgram().observe(this, program -> {
            setTitle(program.getName());
        });

        viewModel.getIsRunning().observe(this, isRunning -> {
            if (isRunning) {
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
            } else {
                // Show the system bar
                displayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

                appBar.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getDisplay().observe(this, displayView::setDisplay);

        viewModel.getEmulationError()
                .compose(lifecycleProvider.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(emulationException -> {
                    Toast.makeText(EmulatorActivity.this,
                            "Error: " + emulationException.toString(), Toast.LENGTH_LONG).show();
                    this.finish();
                });

        displayView.setOnClickListener(view -> {
            viewModel.toggle();
        });

        keyboardView.setKeyListener((key, pressed) -> {
            viewModel.getKeyboard().setKeyPressed(key, pressed);
        });

        viewModel.init(programId);
    }

    @Override
    protected void onPause() {
        viewModel.pause();
        super.onPause();
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

}
