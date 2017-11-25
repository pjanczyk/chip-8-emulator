package com.pjanczyk.chip8emulator.ui.programs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.model.BuiltinProgramList;
import com.pjanczyk.chip8emulator.model.Program;
import com.pjanczyk.chip8emulator.ui.emulator.EmulatorActivity;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProgramsActivity extends AppCompatActivity {

    private static int GET_CONTENT_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        RecyclerView recyclerView = findViewById(R.id.list);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("file/*");
            startActivityForResult(intent, GET_CONTENT_REQUEST_CODE);
        });

        List<ProgramGroup> groups = new ArrayList<>();
        List<Program> builtinPrograms = getBuiltinPrograms();
        groups.add(new ProgramGroup("Built-in programs", builtinPrograms));

        ProgramAdapter adapter = new ProgramAdapter(groups, this::onProgramClicked);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_CONTENT_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = data.getData().getPath();
            //TODO
        }
    }

    public void onProgramClicked(Program program) {
        Intent intent = new Intent(this, EmulatorActivity.class);
        intent.putExtra(EmulatorActivity.EXTRA_PROGRAM, program);
        startActivity(intent);
    }

    private List<Program> getBuiltinPrograms() {
        try {
            InputStream stream = getAssets().open("builtin_programs.xml");

            Persister persister = new Persister();
            BuiltinProgramList builtinPrograms = persister.read(BuiltinProgramList.class, stream);

            stream.close();

            return builtinPrograms.getPrograms();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
