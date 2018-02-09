package com.pjanczyk.chip8emulator.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.pjanczyk.chip8emulator.vm.Chip8State;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Program.class,
                        parentColumns = "id",
                        childColumns = "programId",
                        onDelete = CASCADE
                )
        },
        indices = @Index("programId")
)
public class Save {
    @PrimaryKey
    public final int id;
    public final int programId;
    @Embedded
    public final Chip8State state;
    public final Date createdAt;

    public Save(int id, int programId, Chip8State state, Date createdAt) {
        this.id = id;
        this.programId = programId;
        this.state = state;
        this.createdAt = createdAt;
    }
}
