package smallville7123.textbook;

import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;

/**
 * TextBook is an alternative to android.text.Layout
 *
 * TextBook does not use android.text.Layout because
 *
 * 1. it is closed source (lots of variables are inaccessable)
 * 2. it is impossible to instantiate via a Builder
 * 3. it is impossible to create a Builder due to the Builder itself accessing inaccessable variables which are not included in the SDK
 *
 * (at the very best, if you DO create a custom android.text.Layout, it will be VERY limited in functionality and customization)
 *
 * with that said, TextBook is a 100% Open Source implementation of a basic text layout (engine?) API
 */
@SuppressWarnings("UnqualifiedFieldAccess")
public class TextBook {
    public static final char NEW_LINE_UNIX = '\n';
    public static final char WHITE_SPACE = ' ';
    static String TAG = "TextBook";
    CharSequence text;

    private static int defaultCharBufferSize = 8192;
    private static int defaultExpectedLineLength = 80;

    boolean drawBounds = false;
    int offset_x = 0;
    int offset_y = 0;
    Object lock = new Object();
    TextStats textStats;

    public void setDrawBounds(final boolean drawBounds) {
        this.drawBounds = drawBounds;
    }

    @Nullable
    public static char[] toArray(CharSequence text) {
        if (text == null) return null;
        int length = text.length();
        char[] array = new char[length];
        for (int i = 0; i < length; i++) array[i] = text.charAt(i);
        return array;
    }

    public TextBook(CharSequence charSequence) {
        setText(charSequence);
    }

    public static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    InputStream stream = null;

    public void setText(InputStream inputStream) {
        text = null;
        stream = inputStream;
    }

    public void setText(CharSequence charSequence) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stream = null;
        }
        text = charSequence;
    }

    public void draw(Canvas canvas, TextPaint textPaint) {
        Instant before = Instant.now();
        drawLine(canvas, text == null ? null : text.toString(), textPaint);
        Instant after = Instant.now();
        Log.d(TAG, "draw: completed in " + Duration.between(before, after).toMillis() + " milliseconds");
    }

    private void drawLine(Canvas canvas, String line, TextPaint textPaint) {
        // performance can be improved by caching
        if (textStats == null) {
            Instant before = Instant.now();
                textStats = new TextStats(canvas, line, textPaint, drawBounds) {
                    @Override
                    float getOffsetY() {
                        return offset_y;
                    }
                };
                //
                // building can be slow
                //
                // TODO: accumulative line information building
                //  this will be requires should we support dynamic layout (changing data)
                //
                // TODO: only build information for drawn lines?
                //
                textStats.buildLineInfo(stream);
            Instant after = Instant.now();
            Log.d(TAG, "drawLine: constructed line information for " + textStats.lineCount + " lines and " + textStats.lineLength + " characters in " + Duration.between(before, after).toMillis() + " milliseconds");
        }
        textStats.currentCanvas = canvas;
        drawLine(textStats, textPaint);
    }

    private void drawLine(TextStats textStats, TextPaint alternativeTextPaint) {
        // draw one line extra above and below the screen to enable smooth scrolling
        Instant before = Instant.now();
            textStats.computeLinesToDraw(1, 1);
        Instant after = Instant.now();
        Log.d(TAG, "drawLine: computed lines to draw in " + Duration.between(before, after).toMillis() + " milliseconds");
        before = Instant.now();
        textStats.drawLines(alternativeTextPaint);
        after = Instant.now();
        Log.d(TAG, "drawLine: drawn in " + Duration.between(before, after).toMillis() + " milliseconds");
    }

    // i assume that getHeight would be the final line position
    public int getHeight() {
        synchronized (lock) {
            if (textStats != null) {
                return textStats.lines.get(textStats.lineCount-1).bounds.bottom;
            }
        }
        return 0;
    }

    public int getOffsetX() {
        return offset_x;
    }

    public int getOffsetY() {
        return offset_y;
    }

    public void setOffsetX(int x) {
        if (x < 0) {
            offset_x = 0;
        } else {
            // to deal with x, we need only obtain the line with the longest width
            if (textStats != null) {
                LineStats lineStats = textStats.getLineWithLongestWidth();
                if (lineStats != null) {
                    // our line's width may be smaller than our screen screen width
                    if (lineStats.bounds.right > lineStats.maxWidth) {
                        offset_x = Math.min(x, lineStats.bounds.right - lineStats.maxWidth);
                    } else {
                        offset_x = Math.min(x, lineStats.bounds.right);
                    }
                }
            }
        }
    }

    public void setOffsetY(int y) {
        if (y < 0) {
            offset_y = 0;
        } else {
            // to deal with y, we need only obtain the line with the longest total height
            // this is always the last line
            if (textStats != null) {
                LineStats lineStats = textStats.getLastLine();
                if (lineStats != null) {
                    // our line's height may be smaller than our screen screen height
                    if (lineStats.bounds.bottom > lineStats.maxHeight) {
                        offset_y = Math.min(y, lineStats.bounds.bottom - lineStats.maxHeight);
                    } else {
                        offset_x = Math.min(y, lineStats.bounds.bottom);
                    }
                }
            }
        }
    }

    private static boolean isWhiteAllSpaces(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != WHITE_SPACE) return false;
        }
        return true;
    }
}
