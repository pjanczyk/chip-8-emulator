package com.pjanczyk.chip8emulator;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class Transformations2 {

    public static <T> LiveData<List<T>> flatten(List<LiveData<T>> sources) {
        return new FlattedLiveData<>(sources);
    }

    private static class FlattedLiveData<T> extends MediatorLiveData<List<T>> {
        private final T[] values;
        private final boolean[] valueInitialized;
        private int uninitializedValueCount;

        FlattedLiveData(List<LiveData<T>> sources) {
            int size = sources.size();

            uninitializedValueCount = size;
            valueInitialized = new boolean[size];

            //noinspection unchecked
            values = (T[]) new Object[size];

            for (int i = 0; i < size; i++) {
                final int j = i;
                addSource(sources.get(i), elem -> {
                    values[j] = elem;
                    if (!valueInitialized[j]) {
                        valueInitialized[j] = true;
                        uninitializedValueCount--;
                    }
                    if (uninitializedValueCount == 0) {
                        setValue(ImmutableList.copyOf(values));
                    }
                });
            }
        }
    }

}
