package com.pjanczyk.chip8emulator.model;

import android.app.Application;

import com.annimon.stream.Stream;
import com.google.common.io.ByteStreams;
import com.pjanczyk.chip8emulator.model.xml.BuiltInProgramList;

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
        BuiltInProgramList builtIn = loadXml();

        return Stream.of(builtIn.programs)
                .map(p -> {
                    byte[] bytecode;
                    try (InputStream stream = application.getAssets().open(p.path)) {
                        bytecode = ByteStreams.toByteArray(stream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new Program(0, p.title, true,
                            p.author, p.releaseDate, p.description,
                            bytecode, null);
                })
                .toList();
    }

    private BuiltInProgramList loadXml() {
        Persister persister = new Persister();

        try (InputStream stream = application.getAssets().open("builtin_programs.xml")) {
            return persister.read(BuiltInProgramList.class, stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
