package smallville7123.textbook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import androidx.annotation.NonNull;

import static smallville7123.textbook.TextBook.NEW_LINE_UNIX;

abstract class LineStats {
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
    protected boolean shouldDraw = true;
    boolean drawBounds = false;

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
        lineLength = line == null ? 0 : line.length();
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
        if (line == null) return;
        int xOffsetSaved = lineStats == null ? 0 : lineStats.bounds.right;
        int yOffsetSaved = lineStats == null ? 0 : lineStats.bounds.bottom;
        String l = line.charAt(0) == NEW_LINE_UNIX ? "H" : line;
        textPaint.getTextBounds(l, 0, lineLength, bounds);
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
        obtainWidthsForEachCharacter(line, lineLength);
    }

    int oldLineLength;

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
    public void obtainWidthsForEachCharacter(String line, int lineLength) {
        if (lineLength == 0 || line.isEmpty()) {
            throw new RuntimeException("attempting to obtain widths for 0 characters");
        }
        if (oldLineLength != lineLength) {
            oldLineLength = lineLength;
            widths = new float[lineLength];
        }
        textPaint.getTextWidths(line, widths);
    }

    public void draw(Canvas canvas) {
        draw(canvas, null);
    }

    public void draw(Canvas canvas, TextPaint textPaint) {
        if (shouldDraw) {
            int index = 0;
            int count = lineLength;
            float x = xOffset;
            float y = yOffset - getOffsetY();
            canvas.drawText(line, index, count, x, y, textPaint);

            if (drawBounds) {
                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setColor(0xffff0000);
                canvas.drawRect(bounds, p);
            }
        }
    }

    abstract float getOffsetY();

    char[] chars;
    int charCount = 0;

    public void copyChar(@NonNull char[] chars, int i) {
        chars[i] = line.charAt(i);
        charCount++;
    }

    public void setLine(@NonNull char[] chars) {
        line = String.copyValueOf(chars, 0, charCount);
        lineLength = charCount;
    }

    public void allocateTmp(int lineLength) {
        chars = new char[lineLength];
    }

    protected void deallocateTmp() {
        chars = null;
        charCount = 0;
    }

    protected void wrapCharacters(@NonNull char[] chars, @NonNull float[] widths, int offset) {
        float currentWidth = xOffset;
        for (int i = 0; i < lineLength; i++) {
            float w = currentWidth + widths[i+offset];
            if (w <= maxWidthF) {
                currentWidth = w;
                copyChar(chars, i);
            } else {
                break;
            }
        }
    }
}
