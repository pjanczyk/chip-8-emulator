package com.pjanczyk.chip8emulator.data;

import java.util.Date;

public class SaveInfo {
    public final int id;
    public final int programId;
    public final Date createdAt;

    public SaveInfo(int id, int programId, Date createdAt) {
        this.id = id;
        this.programId = programId;
        this.createdAt = createdAt;
    }
}
