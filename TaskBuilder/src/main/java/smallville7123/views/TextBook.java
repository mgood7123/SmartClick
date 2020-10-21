package smallville7123.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextBook {
    String TAG = "TextBook";
    CharSequence text;

    boolean drawBounds = false;

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
        TextStats textStats = new TextStats(canvas, line, textPaint, drawBounds);
        drawLine(textStats, textPaint);
    }

    private void drawLine(TextStats textStats, TextPaint alternativeTextPaint) {
        textStats.buildLineInfo();
        textStats.drawLines(alternativeTextPaint);
    }

    class LineStats {
        public String line;
        public int lineLength;
        public Rect bounds;
        public TextPaint textPaint;
        public int lineBoundsWidth;
        public int lineBoundsHeight;
        public int maxWidth;
        public float maxWidthF;
        public int maxHeight;
        public float maxHeightF;
        public float[] widths;
        public float xOffset;
        public float yOffset;
        private boolean drawBounds = false;

        public void setDrawBounds(final boolean drawBounds) {
            this.drawBounds = drawBounds;
        }

        public LineStats() {
            this(false);
        }

        public LineStats(boolean drawBounds) {
            this.drawBounds = drawBounds;
        }

        public LineStats(Canvas canvas, String text, TextPaint paint) {
            this(canvas, text, paint, false);
        }

        public LineStats(Canvas canvas, String text, TextPaint paint, boolean drawBounds) {
            this.drawBounds = drawBounds;
            textPaint = paint;
            line = text;
            lineLength = line.length();
            getBounds();

            // TODO: getTextWidths returns a float array, however canvas.getWidth returns an int
            //  how do we account for this?
            //  fow now, just store the canvas width as float
            maxWidth = canvas.getWidth();
            maxWidthF = maxWidth;
            maxHeight = canvas.getHeight();
            maxHeightF = maxHeight;
        }

        void getBounds() {
            getBounds(null);
        }

        void getBounds(LineStats lineStats) {
            if (bounds == null) bounds = new Rect();
            int xOffsetSaved = lineStats == null ? 0 : lineStats.bounds.right;
            int yOffsetSaved = lineStats == null ? 0 : lineStats.bounds.bottom;
            textPaint.getTextBounds(line, 0, lineLength, bounds);
            lineBoundsWidth = bounds.width();
            lineBoundsHeight = bounds.height();
            xOffset = -bounds.left; // + xOffsetSaved;
            yOffset = -bounds.top + yOffsetSaved;
            bounds.offset(0, (int) yOffset);
        }

        /**
         * get our widths for each character.
         * <br>
         * <br>
         * NOTE:
         * <br>
         * <br>
         * using measureText for each character produces incorrect widths.
         * <br>
         * <br>
         * using getTextWidths for the entire string produces correct widths.
         * <br>
         */
        public void obtainWidthsForEachCharacter() {
            if (widths == null) widths = new float[lineLength];
            textPaint.getTextWidths(line, widths);
        }

        public void draw(Canvas canvas) {
            draw(canvas, null);
        }

        public void draw(Canvas canvas, TextPaint textPaint) {
            int index = 0;
            int count = lineLength;
            float x = xOffset;
            float y = yOffset;
            canvas.drawText(line, index, count, x, y, textPaint);

            if (drawBounds) {
                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setColor(0xffff0000);
                canvas.drawRect(bounds, p);
            }
        }
    }


    class TextStats extends LineStats {
        public Canvas currentCanvas;
        boolean wrapped;
        ArrayList<LineStats> lines;
        public int lineCount;

        @Override
        public void setDrawBounds(final boolean drawBounds) {
            for (LineStats lineStats : lines) {
                lineStats.drawBounds = drawBounds;
            }
        }

        public TextStats() {
            this(false);
        }

        public TextStats(boolean drawBounds) {
            super(drawBounds);
        }

        public TextStats(Canvas canvas, String text, TextPaint paint) {
            this(canvas, text, paint, false);
        }

        public TextStats(Canvas canvas, String text, TextPaint paint, boolean drawBounds) {
            super(canvas, text, paint, drawBounds);
            currentCanvas = canvas;
            wrapped = lineBoundsWidth > maxWidth;
        }

        public void buildLineInfo() {
            if (wrapped) {
                int consumed = 0;
                if (lines == null) lines = new ArrayList<>();
                LineStats lastLineStats = null;
                LineStats lineStats = null;
                while (consumed != lineLength) {
                    lastLineStats = lineStats;
                    lineStats = new LineStats(drawBounds);
                    // process each character individually
                    // we do not draw each character, but instead build up a string, and then draw it

                    lineStats.line = consumed == 0 ? line : line.substring(consumed);
                    lineStats.lineLength = lineLength - consumed;
                    lineStats.textPaint = textPaint;
                    lineStats.obtainWidthsForEachCharacter();

                    char[] chars = new char[lineStats.lineLength];

                    lineStats.xOffset = xOffset;

                    float currentWidth = lineStats.xOffset;
                    int charCount = 0;

                    lineStats.maxWidth = maxWidth;
                    lineStats.maxWidthF = maxWidthF;

                    for (int i = 0; i < lineStats.lineLength; i++) {
                        float w = currentWidth + lineStats.widths[i];
                        if (w <= lineStats.maxWidthF) {
                            currentWidth = w;
                            chars[i] = lineStats.line.charAt(i);
                            charCount++;
                        } else {
                            break;
                        }
                    }

                    // the bounds of the new text may be different than the bounds of the old text
                    lineStats.line = String.copyValueOf(chars, 0, charCount);
                    lineStats.lineLength = charCount;
                    lineCount++;
                    lineStats.getBounds(lastLineStats);
                    lineStats.maxHeight = maxHeight;
                    lineStats.maxHeightF = maxHeightF;
                    // dont produce lines that would be drawn past the maximum canvas height
                    if (lineStats.yOffset > maxHeight) break;
                    lines.add(lineStats);
                    consumed += charCount;
                }
            }
        }

        /**
         * Draws all lines
         * <br>
         * does nothing there are no lines to draw
         */
        public void drawLines() {
            drawLines(null);
        }

        /**
         * Draws all lines
         * <br>
         * does nothing there are no lines to draw
         * @param  textPaint the paint to use when drawing
         */
        public void drawLines(TextPaint textPaint) {
            if (lineCount != 0) {
                for (LineStats lineStats : lines) {
                    lineStats.draw(currentCanvas, textPaint);
                }
            }
        }

        /**
         * Draws a line of text associated with given line number.
         * <br>
         * does nothing if the line does not exist
         *
         * @param  lineNumber index of the element to return
         */
        public void drawLine(int lineNumber) {
            drawLine(lineNumber, null);
        }

        /**
         * Draws a line of text associated with given line number.
         * <br>
         * does nothing if the line does not exist
         *
         * @param  lineNumber index of the element to return
         * @param  textPaint the paint to use when drawing
         */
        public void drawLine(int lineNumber, TextPaint textPaint) {
            if (lineNumber >= lineCount) return;
            lines.get(lineNumber).draw(currentCanvas, textPaint);
        }
    }
}
