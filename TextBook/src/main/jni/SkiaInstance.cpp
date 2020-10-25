//
// Created by matthew good on 24/10/20.
//

#include <include/core/SkFont.h>
#include <include/core/SkTextBlob.h>
#include "SkiaInstance.h"
#include "JniHelpers.h"

void SkiaInstance::createCanvas(int width, int height) {
    this->width = width;
    this->height = height;
    info = SkImageInfo::MakeN32Premul(width, height);
    /**
     * Android Bitmap
     *
     * stride – The number of colors in pixels[] to skip between rows.
     *          Normally this value will be the same as the width of the bitmap,
     *          but it can be larger (or negative).
     */
    stride = info.width();
    rowBytes = info.minRowBytes();
    pixelMemoryLength = info.computeByteSize(rowBytes);
    pixelMemory = new uint32_t[pixelMemoryLength];
    surface = SkSurface::MakeRasterDirect(info, pixelMemory, rowBytes);
    canvas = surface->getCanvas();
}

jintArray SkiaInstance::getPixels(JNIEnv *env) {
    jintArray array = env->NewIntArray(pixelMemoryLength);
    env->SetIntArrayRegion(array, 0, pixelMemoryLength, reinterpret_cast<const jint *>(pixelMemory));
    return array;
}

SkiaInstance & SkiaInstance::getInstance(jlong native_skia_ptr) {
    return reinterpret_cast<SkiaInstance*>(native_skia_ptr)[0];
}

SkPaint & SkiaInstance::getPaint(jlong paint) {
    return reinterpret_cast<SkPaint*>(paint)[0];
}

SkFont & SkiaInstance::getFont(jlong font) {
    return reinterpret_cast<SkFont*>(font)[0];
}

void SkiaInstance::drawText(const char *text, int index, int count, float x, float y, SkPaint & paint,
                            SkFont & font) {
    sk_sp<SkTextBlob> blob1 = SkTextBlob::MakeFromString(text, font);
    canvas->drawTextBlob(blob1.get(), x, y, paint);
}

void SkiaInstance::clear(SkColor color) {
    canvas->clear(color);
}
