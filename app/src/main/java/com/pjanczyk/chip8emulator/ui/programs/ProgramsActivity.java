package com.pjanczyk.chip8emulator.ui.programs;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.common.io.ByteStreams;
import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.model.ProgramInfo;
import com.pjanczyk.chip8emulator.ui.emulator.EmulatorActivity;

import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class ProgramsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GET_CONTENT = 100;
    private static final String TAG = "ProgramsActivity";

    @Inject ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton buttonOpen;
    @BindView(R.id.list) RecyclerView recyclerView;

    private ProgramsViewModel viewModel;
    private ProgramAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programs);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProgramsViewModel.class);

        setSupportActionBar(toolbar);

        buttonOpen.setOnClickListener(view -> onButtonOpenClicked());

        viewModel.getProgramGroups().observe(this, programGroups -> {
            adapter = new ProgramAdapter(programGroups, this::onProgramClicked);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != REQUEST_CODE_GET_CONTENT || resultCode != RESULT_OK) return;

        Uri uri = intent.getData();
        if (uri == null) return;

        byte[] bytecode;

        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            bytecode = ByteStreams.toByteArray(stream);
        } catch (Exception e) {
            Log.e(TAG, "Failed to import program", e);
            Toast.makeText(this, "Failed to import program", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        String name = uri.getLastPathSegment();
        viewModel.addImportedProgram(name, bytecode);
    }

    private void onButtonOpenClicked() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("file/*");
        startActivityForResult(intent, REQUEST_CODE_GET_CONTENT);
    }

    private void onProgramClicked(ProgramInfo program) {
        Intent intent = new Intent(this, EmulatorActivity.class);
        intent.putExtra(EmulatorActivity.EXTRA_PROGRAM_ID, program.getId());
        startActivity(intent);
    }
}
