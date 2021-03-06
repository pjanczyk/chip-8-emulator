package com.pjanczyk.chip8emulator.ui.emulator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pjanczyk.chip8emulator.R;
import com.pjanczyk.chip8emulator.data.source.KeyBinding;

public class KeyboardView extends TableLayout {

    private static final int COLUMNS = 4;
    private static final int ROWS = 4;

    private static final String[] KEY_NAMES = {
            "1", "2", "3", "C",
            "4", "5", "6", "D",
            "7", "8", "9", "E",
            "A", "0", "B", "F"
    };
    private static final int[] KEY_VALUES = {
            0x1, 0x2, 0x3, 0xC,
            0x4, 0x5, 0x6, 0xD,
            0x7, 0x8, 0x9, 0xE,
            0xA, 0x0, 0xB, 0xF
    };
    private final TextView[] buttonDescriptions = new TextView[16];

    private KeyListener keyListener;

    @SuppressLint("ClickableViewAccessibility")
    private final OnTouchListener buttonTouchListener = (View v, MotionEvent event) -> {
        if (keyListener != null) {
            int key = (int) v.getTag();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                keyListener.onKeyStateChanged(key, true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                keyListener.onKeyStateChanged(key, false);
            }
        }
        return false;
    };

    public KeyboardView(Context context) {
        this(context, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);

        for (int r = 0; r < ROWS; r++) {
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));

            for (int c = 0; c < COLUMNS; c++) {

                int index = r * COLUMNS + c;
                ViewGroup button = (ViewGroup) inflater.inflate(
                        R.layout.keyboard_button, null, false);

                button.setLayoutParams(
                        new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));

                button.<TextView>findViewById(R.id.text_button_name).setText(KEY_NAMES[index]);

                button.setTag(KEY_VALUES[index]);
                button.setOnTouchListener(buttonTouchListener);

                buttonDescriptions[KEY_VALUES[index]] =
                        button.findViewById(R.id.text_button_description);

                row.addView(button);
            }

            this.addView(row);
        }
    }

    public void setKeyBinding(KeyBinding keyBinding) {
        for (int i = 0; i < 16; i++) {
            String description = keyBinding.getKeyDescription(i);
            if (description != null) {
                buttonDescriptions[i].setText(description);
            }
        }
    }

    public void setKeyListener(KeyListener listener) {
        keyListener = listener;
    }

    public interface KeyListener {
        void onKeyStateChanged(int key, boolean pressed);
    }

}
