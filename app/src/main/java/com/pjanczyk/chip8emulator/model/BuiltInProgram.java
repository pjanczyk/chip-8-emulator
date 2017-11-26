package com.pjanczyk.chip8emulator.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.IOException;
import java.io.InputStream;

public class BuiltInProgram implements Program, Parcelable {

    @Attribute
    private String path;

    @Element
    private String title;

    @Element(required = false)
    private String description;

    @Element(required = false)
    private String author;

    @Element(required = false)
    private String releaseDate;

    private BuiltInProgram() {}

    @Override
    public String getDisplayName() {
        return title;
    }

    @Override
    public InputStream openFile(Context context) throws IOException {
        return context.getAssets().open(path);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    private BuiltInProgram(Parcel in) {
        path = in.readString();
        title = in.readString();
        description = in.readString();
        author = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BuiltInProgram> CREATOR = new Creator<BuiltInProgram>() {
        @Override
        public BuiltInProgram createFromParcel(Parcel in) {
            return new BuiltInProgram(in);
        }

        @Override
        public BuiltInProgram[] newArray(int size) {
            return new BuiltInProgram[size];
        }
    };
}
