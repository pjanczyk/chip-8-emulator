package com.pjanczyk.chip8emulator.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.pjanczyk.chip8emulator.vm.Chip8State;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = Program.class,
                parentColumns = "id",
                childColumns = "programId",
                onDelete = CASCADE)
})
public class Save {
    @PrimaryKey
    public final int programId;
    @Embedded @NonNull
    public final Chip8State state;

    public Save(int programId, @NonNull Chip8State state) {
        this.programId = programId;
        this.state = state;
    }
}
