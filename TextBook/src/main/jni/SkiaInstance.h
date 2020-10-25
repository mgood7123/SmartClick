//
// Created by matthew good on 24/10/20.
//

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedGlobalDeclarationInspection"
#ifndef SMARTCLICK_SKIAINSTANCE_H
#define SMARTCLICK_SKIAINSTANCE_H

#include <include/core/SkBitmap.h>
#include <include/core/SkCanvas.h>
#include <include/core/SkSurface.h>
#include <include/core/SkFont.h>
#include <include/core/SkPaint.h>
#include <jni.h>

class SkiaInstance {
    SkBitmap tmp;
    SkCanvas * canvas = nullptr;
public:
    void createCanvas(int width, int height);

    static SkiaInstance & getInstance(jlong native_skia_ptr);
    static SkPaint & getPaint(jlong paint);
    static SkFont & getFont(jlong font);

    jlong getBitmap();

    void drawText(const char * text, int index, int count, float x, float y,
            SkPaint & paint, SkFont & font);

    int width = 0;
    int height = 0;
};

#endif //SMARTCLICK_SKIAINSTANCE_H

#pragma clang diagnostic pop