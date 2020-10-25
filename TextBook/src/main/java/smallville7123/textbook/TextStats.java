package smallville7123.textbook;

import android.graphics.Bitmap;
import android.text.TextPaint;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

abstract class TextStats extends LineStats {
    public static final char TAB = '\t';
    public Skia currentSkia;
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

    public TextStats(Skia skia, String text, TextPaint paint) {
        this(skia, text, paint, false);
    }

    public TextStats(Skia skia, String text, TextPaint paint, boolean drawBounds) {
        super(skia, text, paint, drawBounds);
        currentSkia = skia;
        wrapped = lineBoundsWidth > maxWidth;
    }

    public void buildLineInfo(InputStream stream) {
        StringBuilder tmp = new StringBuilder();

        if (stream == null) {
            process(tmp, line, line.length());
        } else {
            // reading from a stream and processing as we go
            // seems to increase performance as opposed to
            // reading the entire stream into a string
            // and then processing that string
            byte buf[] = new byte[1024];
            try {
                int len;
                while (true) {
                    len = stream.read(buf);
                    if (len == -1) return;
                    String line = new String(buf, 0, len, StandardCharsets.UTF_8);
                    process(tmp, line, line.length());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void process(StringBuilder tmp, String data, int dataLength) {
        for (int i = 0; i < dataLength; i++) {
            char c = data.charAt(i);
            switch (c) {
                case TextBook.NEW_LINE_UNIX:
                    if (tmp.length() != 0) {
                        buildLineInfoInternal(tmp);
                    } else buildLineInfoInternal(TextBook.NEW_LINE_UNIX_STRING, 1);
                    break;
                case TAB:
                    tmp.append("    ");
                    break;
                default:
                    tmp.append(c);
                    break;
            }
        }
        if (tmp.length() != 0) buildLineInfoInternal(tmp);
    }

    public void buildLineInfoInternal(StringBuilder tmp) {
        String s = tmp.toString();
        int l = s.length();
        buildLineInfoInternal(s, l);
        tmp.delete(0, l);
        tmp.trimToSize();
    }

    public void buildLineInfoInternal(String line) {
        buildLineInfoInternal(line, line.length());
    }

    LineStats lastLineStats;
    LineStats lineStats = null;

    public void buildLineInfoInternal(String line, int lineLength) {
        int consumed = 0;
        if (lines == null) lines = new ArrayList<>();
        // invoke this only once, and then offset by index
        obtainWidthsForEachCharacter(line, lineLength);
        allocateTmp(lineLength);
        while (consumed != lineLength) {
            lastLineStats = lineStats;
            // we need to allocate here because we build up a list of lines
            // adding each line at the end of this loop
            lineStats = new LineStats(drawBounds) {
                @Override
                float getOffsetY() {
                    return TextStats.this.getOffsetY();
                }
            };
            // process each character individually
            // we do not draw each character, but instead build up a string, and then draw it

            lineStats.line = consumed == 0 ? line : line.substring(consumed);
            lineStats.lineLength = lineLength - consumed;
            lineStats.textPaint = textPaint;
            lineStats.xOffset = xOffset;
            lineStats.maxWidth = maxWidth;
            lineStats.maxWidthF = maxWidthF;

            // offset by consumed
            lineStats.wrapCharacters(chars, widths, consumed);

            // the bounds of the new text may be different than the bounds of the old text
            lineStats.setLine(chars);
            this.lineLength += lineStats.lineLength;
            lineCount++;
            lineStats.getBounds(lastLineStats);
            lineStats.maxHeight = maxHeight;
            lineStats.maxHeightF = maxHeightF;
            lines.add(lineStats);
            consumed += lineStats.lineLength;
        }
        deallocateTmp();
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
                lineStats.draw(currentSkia, textPaint);
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
        lines.get(lineNumber).draw(currentSkia, textPaint);
    }

    @Nullable
    public LineStats getFirstLine() {
        return lineCount != 0 ? lines.get(0) : null;
    }

    @Nullable public LineStats getLastLine() {
        return lineCount != 0 ? lines.get(lineCount - 1) : null;
    }

    @Nullable public LineStats getLineWithLongestHeight() {
        int currentHeight = 0;
        LineStats currentLine = null;
        for (LineStats lineStats : lines) {
            int height = lineStats.bounds.bottom;
            if (height > currentHeight) {
                currentHeight = height;
                currentLine = lineStats;
            }
        }
        return currentLine;
    }

    @Nullable public LineStats getLineWithLongestWidth() {
        int currentWidth = 0;
        LineStats currentLine = null;
        for (LineStats lineStats : lines) {
            int width = lineStats.bounds.right;
            if (width > currentWidth) {
                currentWidth = width;
                currentLine = lineStats;
            }
        }
        return currentLine;
    }

    public void computeLinesToDraw() {
        computeLinesToDraw(0, 0);
    }

    public void computeLinesToDraw(int extraLinesToDrawAbove, int extraLinesToDrawBelow) {
        boolean foundStart = false;
        boolean foundEnd = false;
        float offset = getOffsetY();
        float heightOffset = maxHeightF + offset;
        int size = lines.size();

        for (int i = 0; i < size; i++) {
            LineStats line = lines.get(i);
            // do not draw lines that would end up being
            // be drawn past the maximum canvas height
            boolean top =  line.bounds.top >= offset;
            boolean bottom = line.bounds.bottom <= heightOffset;
            line.shouldDraw = top && bottom;
            if (line.shouldDraw) {
                if (!foundStart) {
                    foundStart = true;
                    for (int i1 = i-1; i1 >= 0 && i1 > (i-1-extraLinesToDrawAbove); i1--) {
                        lines.get(i1).shouldDraw = true;
                    }
                }
            } else {
                if (foundStart && !foundEnd) {
                    if (i + 1 <= size) {
                        foundEnd = true;
                        for (int i1 = i; i1 < i+extraLinesToDrawBelow && i1 < size; i1++) {
                            lines.get(i1).shouldDraw = true;
                        }
                    }
                }
            }
        }
    }
}
