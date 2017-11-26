package com.pjanczyk.chip8emulator.model;

import android.content.Context;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class ProgramRepository {

    private Context context;

    public ProgramRepository(Context context) {
        this.context = context;
    }

    public List<Program> getBuiltinPrograms() {
        try {
            InputStream stream = context.getAssets().open("builtin_programs.xml");

            Persister persister = new Persister();
            BuiltInProgramList builtinPrograms = persister.read(BuiltInProgramList.class, stream);

            stream.close();

            return builtinPrograms.getPrograms();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Program> getRecentPrograms() {
        // TODO: return list of recent programs
        return Collections.emptyList();
    }

    public void addRecent(Program program) {
        // TODO: add recent program to database
    }
}
