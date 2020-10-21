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
    String TAG = "TextBook";
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
        canvasWidth = canvas.getWidth();
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
        drawLine(canvas, line, textPaint, null);
    }

    class TextStats {

    }

    private void drawLine(Canvas canvas, String line, TextPaint textPaint, TextStats textStats) {
        // mays as well obtain the bounds of the entire text
        int characters = line.length();
        textPaint.getTextBounds(line, 0, characters, bounds);
        if (bounds.width() > canvasWidth) {

            // bounds.width() exceeds canvas width
            // process each character individually
            // we do not draw each character, but instead build up a string, and then draw it

            char[] chars = new char[characters];
            float[] widths = new float[characters];

            // get our widths for each character
            //
            // NOTE: using measureText for each character produces incorrect widths
            //
            // while getTextWidths for the entire string produces correct widths
            //

            textPaint.getTextWidths(line, widths);

            // TODO: getTextWidths returns a float array, however canvas.getWidth returns an int
            //  how do we account for this?
            //  fow now, just store the canvas width as float

            int charCount = 0;
            float currentWidth = -bounds.left;
            float cw = canvasWidth;
            for (int i = 0; i < characters; i++) {
                float w = currentWidth + widths[i];
                if (w <= cw) {
                    currentWidth = w;
                    chars[i] = line.charAt(i);
                    charCount++;
                } else {
                    break;
                }
            }
            Log.d(TAG, "charCount = [" + charCount + "]");
            // the bounds of the new text may be different than the bounds of the old text
            textPaint.getTextBounds(chars, 0, charCount, bounds);
            int index = 0;
            int count = charCount;
            float x = -bounds.left;
            float y = -bounds.top;
            canvas.drawText(chars, index, count, x, y, textPaint);
        } else {
            // bounds.width() does not exceed canvas width
            int start = 0;
            int end = characters;
            float x = -bounds.left;
            float y = -bounds.top;
            canvas.drawText(line, start, end, x, y, textPaint);
        }
    }
}
