package com.pjanczyk.chip8emulator.model;// Author: Piotr Janczyk, 27.03.16

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public interface Program extends Parcelable {

    String getName();

    int getSize();

    boolean isBuiltin();

    String getDocs();

    String getAuthor();

    String getYear();

    @NonNull
    byte[] readBytecode(Context context);
}
