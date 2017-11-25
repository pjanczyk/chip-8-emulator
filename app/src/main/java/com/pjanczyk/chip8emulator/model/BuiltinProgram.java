package com.pjanczyk.chip8emulator.model;// Author: Piotr Janczyk, 29.03.16

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.io.FileInputStream;
import java.io.IOException;

public class BuiltinProgram implements Program, Parcelable {

    @Element
    private String name;
    @Attribute
    private String path;
    @Element(required = false)
    private int size;
    @Element(required = false)
    private String author;
    @Element(required = false)
    private String year;
    @Element(required = false)
    private String docs;

    private BuiltinProgram() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public String getDocs() {
        return docs;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getYear() {
        return year;
    }

    @Override
    @NonNull
    public byte[] readBytecode(Context context) {
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(path);
            int size = (int) fileDescriptor.getLength();
            FileInputStream stream = fileDescriptor.createInputStream();

            byte[] bytecode = new byte[size];
            stream.read(bytecode);
            stream.close();
            fileDescriptor.close();

            return bytecode;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected BuiltinProgram(Parcel in) {
        name = in.readString();
        path = in.readString();
        size = in.readInt();
        author = in.readString();
        year = in.readString();
        docs = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeInt(size);
        dest.writeString(author);
        dest.writeString(year);
        dest.writeString(docs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BuiltinProgram> CREATOR = new Creator<BuiltinProgram>() {
        @Override
        public BuiltinProgram createFromParcel(Parcel in) {
            return new BuiltinProgram(in);
        }

        @Override
        public BuiltinProgram[] newArray(int size) {
            return new BuiltinProgram[size];
        }
    };
}
