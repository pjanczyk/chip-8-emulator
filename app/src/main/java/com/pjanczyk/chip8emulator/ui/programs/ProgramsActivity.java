package com.pjanczyk.chip8emulator.ui.programs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.common.collect.Lists;
import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.model.ExternalProgram;
import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.model.ProgramRepository;
import com.pjanczyk.chip8emulator.ui.emulator.EmulatorActivity;

import java.util.List;

public class ProgramsActivity extends AppCompatActivity {

    private static int REQUEST_CODE_GET_CONTENT = 100;

    private ProgramRepository repository;
    private ProgramAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        RecyclerView recyclerView = findViewById(R.id.list);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(view -> onButtonOpenClicked());

        repository = new ProgramRepository(this);

        List<ProgramGroup> groups = Lists.newArrayList(
                new ProgramGroup("Recent programs", repository.getRecentPrograms()),
                new ProgramGroup("Built-in programs", repository.getBuiltinPrograms())
        );

        adapter = new ProgramAdapter(groups, this::onProgramClicked);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE_GET_CONTENT && resultCode == RESULT_OK) {
            Uri uri = intent.getData();
            ExternalProgram program = new ExternalProgram(uri);

            byte[] bytecode = program.readBytecode(this);
            // TODO: add program to list
        }
    }

    private void onButtonOpenClicked() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("file/*");
        startActivityForResult(intent, REQUEST_CODE_GET_CONTENT);
    }

    private void onProgramClicked(Program program) {
        Intent intent = new Intent(this, EmulatorActivity.class);
        intent.putExtra(EmulatorActivity.EXTRA_PROGRAM, program);
        startActivity(intent);
    }
}
