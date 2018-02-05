package com.pjanczyk.chip8emulator.model;

import android.app.Application;

import com.annimon.stream.Stream;
import com.google.common.io.ByteStreams;

import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

public class BuiltInProgramsProvider {
    private final Application application;

    @Inject
    public BuiltInProgramsProvider(Application application) {
        this.application = application;
    }

    public List<Program> getBuiltInPrograms() {
        List<BuiltInProgram> builtInPrograms = loadFromXml();

        return Stream.of(builtInPrograms)
                .map(bp -> {
                    byte[] bytecode;
                    try (InputStream stream = application.getAssets().open(bp.getPath())) {
                        bytecode = ByteStreams.toByteArray(stream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new Program(0, bp.getTitle(), true,
                            bp.getAuthor(), bp.getReleaseDate(), bp.getDescription(),
                            bytecode, null);
                })
                .toList();
    }

    private List<BuiltInProgram> loadFromXml() {
        try {
            InputStream stream = application.getAssets().open("builtin_programs.xml");

            Persister persister = new Persister();
            BuiltInProgramList builtinPrograms = persister.read(BuiltInProgramList.class, stream);

            stream.close();

            return builtinPrograms.getPrograms();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
