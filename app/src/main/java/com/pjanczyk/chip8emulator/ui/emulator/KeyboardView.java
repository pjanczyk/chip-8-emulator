package com.pjanczyk.chip8emulator.ui.emulator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class KeyboardView extends TableLayout {

    private static final int COLUMNS = 4;
    private static final int ROWS = 4;
    private static final String[] KEY_DESCRIPTIONS = {
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

    private KeyListener keyListener;
    private OnTouchListener buttonTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (keyListener != null) {
                int key = (int) v.getTag();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    keyListener.onKeyStateChanged(key, true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    keyListener.onKeyStateChanged(key, false);
                }
            }

            return false;
        }
    };

    public KeyboardView(Context context) {
        this(context, null);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        for (int r = 0; r < ROWS; r++) {
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));

            for (int c = 0; c < COLUMNS; c++) {

                int index = r * COLUMNS + c;
                Button button = new Button(getContext());
                button.setBackgroundColor(0xff000000);
                button.setTextColor(0xffe5e5e5);
                button.setLayoutParams(
                        new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f));

                button.setText(KEY_DESCRIPTIONS[index]);
                button.setTag(KEY_VALUES[index]);

                button.setOnTouchListener(buttonTouchListener);

                row.addView(button);
            }

            this.addView(row);
        }
    }

    public void setKeyListener(KeyListener listener) {
        keyListener = listener;
    }

    public interface KeyListener {
        void onKeyStateChanged(int key, boolean pressed);
    }

}
