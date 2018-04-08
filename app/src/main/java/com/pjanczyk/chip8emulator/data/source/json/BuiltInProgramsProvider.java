package com.pjanczyk.chip8emulator.data.source.json;

import android.app.Application;

import com.annimon.stream.Stream;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pjanczyk.chip8emulator.data.Program;
import com.pjanczyk.chip8emulator.data.source.KeyBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

public class BuiltInProgramsProvider {
    private final Application application;

    @Inject
    public BuiltInProgramsProvider(Application application) {
        this.application = application;
    }

    public List<Program> getBuiltInPrograms() {
        List<BuiltInProgram> builtIn = readJson();

        return Stream.of(builtIn)
                .map(program -> {
                    byte[] bytecode;
                    try (InputStream stream = application.getAssets().open(program.path)) {
                        bytecode = ByteStreams.toByteArray(stream);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new Program.Builder()
                            .setName(program.name)
                            .setBuiltIn(true)
                            .setAuthor(program.author)
                            .setReleaseDate(program.date)
                            .setDescription(program.description)
                            .setKeyBinding(new KeyBinding(program.keys))
                            .setBytecode(bytecode)
                            .build();
                })
                .toList();
    }

    private List<BuiltInProgram> readJson() {
        Gson gson = new GsonBuilder().create();

        try (InputStream stream = application.getAssets().open("roms.json");
             InputStreamReader reader = new InputStreamReader(stream)) {

            return gson.fromJson(reader, new TypeToken<List<BuiltInProgram>>() { }.getType());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
