package com.pjanczyk.chip8emulator.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.InputStream;

public class ExternalProgram implements Program, Parcelable {

    private Uri uri;

    public ExternalProgram(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public String getDisplayName() {
        return uri.getLastPathSegment();
    }

    @Override
    public InputStream openFile(Context context) throws IOException {
        return context.getContentResolver().openInputStream(uri);
    }

    private ExternalProgram(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExternalProgram> CREATOR = new Creator<ExternalProgram>() {
        @Override
        public ExternalProgram createFromParcel(Parcel in) {
            return new ExternalProgram(in);
        }

        @Override
        public ExternalProgram[] newArray(int size) {
            return new ExternalProgram[size];
        }
    };
}
