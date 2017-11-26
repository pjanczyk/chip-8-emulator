package com.pjanczyk.chip8emulator.model;

import android.content.Context;
import android.os.Parcelable;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;

public interface Program extends Parcelable {
    String getDisplayName();
    InputStream openFile(Context context) throws IOException;

    default byte[] readBytecode(Context context) {
        try (InputStream stream = openFile(context)) {
            return ByteStreams.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
