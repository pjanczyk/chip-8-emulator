package com.pjanczyk.chip8emulator.ui.emulator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pjanczyk.chip8emulator.R;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindInt;
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
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container_display_overlay) View containerDisplayOverlay;
    @BindView(R.id.container_description) View containerDescription;
    @BindView(R.id.button_restart) Button buttonRestart;
    @BindView(R.id.button_quick_save) Button buttonQuickSave;
    @BindView(R.id.button_quick_restore) Button buttonQuickRestore;
    @BindView(R.id.button_options) Button buttonOptions;
    @BindView(R.id.button_pause_resume) ImageButton buttonPauseResume;
    @BindView(R.id.text_name) TextView textName;
    @BindView(R.id.text_author) TextView textAuthor;
    @BindView(R.id.text_release_date) TextView textReleaseData;
    @BindView(R.id.text_description) TextView textDescription;

    @BindInt(android.R.integer.config_shortAnimTime) int shortAnimationTime;
    @BindDimen(R.dimen.animation_translation) float animationTranslation;

    private EmulatorViewModel viewModel;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);
        ButterKnife.bind(this);

        findViewById(android.R.id.content).setOnApplyWindowInsetsListener((v, insets) -> {
            int insetLeft = insets.getSystemWindowInsetLeft();
            int insetTop = insets.getSystemWindowInsetTop();
            int insetRight = insets.getSystemWindowInsetRight();
            int insetBottom = insets.getSystemWindowInsetBottom();
            containerDisplayOverlay.setPadding(insetLeft, insetTop, insetRight, 0);
            containerDescription.setPadding(insetLeft, 0, insetRight, insetBottom);
            insets.consumeStableInsets();
            return insets;
        });

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EmulatorViewModel.class);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(v -> {
            this.onBackPressed();
        });

        int programId = getIntent().getIntExtra(EXTRA_PROGRAM_ID, 0);
        viewModel.init(programId);

        viewModel.getProgram().observe(this, program -> {
            setTitle(program.name);
            textName.setText(program.name);
            textAuthor.setText(program.author);
            textReleaseData.setText(program.releaseDate);
            textDescription.setText(program.description);
            buttonQuickRestore.setEnabled(program.quickSave != null);
        });

        viewModel.getIsRunning().observe(this, isRunning -> {
            buttonPauseResume.setVisibility(isRunning ? View.INVISIBLE : View.VISIBLE);

            if (isRunning) {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                displayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                containerDisplayOverlay.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationTime)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                containerDisplayOverlay.setVisibility(View.GONE);
                            }
                        });

                containerDescription.animate()
                        .alpha(0f)
                        .translationY(animationTranslation)
                        .setDuration(shortAnimationTime)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                containerDescription.setVisibility(View.GONE);
                            }
                        });

            } else {
                // Show the system bar
                displayView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

                containerDisplayOverlay.setAlpha(0f);
                containerDisplayOverlay.setVisibility(View.VISIBLE);
                containerDisplayOverlay.animate()
                        .alpha(1f)
                        .setDuration(shortAnimationTime)
                        .setListener(null);

                containerDescription.setAlpha(0f);
                containerDescription.setVisibility(View.VISIBLE);
                containerDescription.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(shortAnimationTime)
                        .setListener(null);
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

        displayView.setOnClickListener(view -> viewModel.toggle());

        keyboardView.setKeyListener((key, pressed) -> {
            viewModel.getKeyboard().setKeyPressed(key, pressed);
        });

        buttonRestart.setOnClickListener(v -> viewModel.restart());
        buttonQuickSave.setOnClickListener(v -> viewModel.quickSave());
        buttonQuickRestore.setOnClickListener(v -> viewModel.quickRestore());
        buttonOptions.setOnClickListener(v -> viewModel.options());
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.resume();
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
