package smallville7123.views;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Arrays;

public class TextBook {
    CharSequence text;
    Rect bounds = new Rect();
    int canvasWidth;
    int canvasHeight;

    @Nullable
    public static char[] toArray(CharSequence text) {
        if (text == null) return null;
        int length = text.length();
        char[] array = new char[length];
        for (int i = 0; i < length; i++) array[i] = text.charAt(i);
        return array;
    }

    public TextBook(CharSequence charSequence) {
        text = charSequence;
    }

    public void draw(Canvas canvas, TextPaint textPaint) {
        char[] text = toArray(this.text);
        BufferedReader reader = new BufferedReader(new CharArrayReader(text, 0, text.length));
        try {
            String line = reader.readLine();
            while (line != null) {
                drawLine(canvas, line, textPaint);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawLine(Canvas canvas, String line, TextPaint textPaint) {
        canvasWidth = canvas.getWidth();
        int characters = line.length();
        textPaint.getTextBounds(line, 0, characters, bounds);
        int i = 0;
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);
        assertBounds(line, 0, ++i, textPaint);

//        drawLine(canvas, line, textPaint, textBoundsWidth > canvasWidth);
    }

    private void assertBounds(String line, int start, int end, TextPaint textPaint) {
        int textBoundsWidth = 0;
        int currentWidth = 0;
        String sample = line.substring(start, end);
        textBoundsWidth += textPaint.measureText(sample, 0, sample.length());
        for (int i = 0; i < sample.length();) {
            currentWidth += textPaint.measureText(sample, i, ++i);
        }

        if (currentWidth != textBoundsWidth) {
            // width mismatch: expected: 6864, received: 6875
            throw new RuntimeException(
                    "sample: " + Arrays.toString(toArray(sample)) + ", width mismatch: expected: " + textBoundsWidth + ", received: " + currentWidth
            );
        }
        Log.d("TextBook", "sample: " + Arrays.toString(toArray(sample)) + ", assertBounds() width matches: expected: " + textBoundsWidth + ", received: " + currentWidth);
    }

    private void drawLine(Canvas canvas, String line, TextPaint textPaint, boolean wrap) {
        if (wrap) {
            // bounds.width() exceeds canvas width
            // process each character individually
            // do not draw each character, instead build up a string, then draw it
            int characters = line.length();
            char[] chars = new char[characters];
            int charCount = 0;
            int currentWidth = 0;
            int maxWidth = canvasWidth;
            for (int i = 0; i < characters; i++) {
                textPaint.getTextBounds(line, i, i+1, bounds);
                int t = currentWidth + bounds.width();
                if (t <= canvasWidth) {
                    currentWidth = t;
                    chars[i] = line.charAt(i);
                    charCount++;
                } else {
                    break;
                }
            }
            // about 44 to 45
            canvas.drawText(chars, 0, charCount++, -bounds.left, -bounds.top, textPaint);
        } else {
            int start = 0;
            int end = line.length();
            float x = -bounds.left;
            float y = -bounds.top;
            canvas.drawText(line, start, end, x, y, textPaint);
        }
    }
}
