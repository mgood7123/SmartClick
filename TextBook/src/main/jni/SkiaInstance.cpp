//
// Created by matthew good on 24/10/20.
//

#include <include/core/SkFont.h>
#include <include/core/SkTextBlob.h>
#include "SkiaInstance.h"

void SkiaInstance::createCanvas(int width, int height) {
    tmp.allocPixels(SkImageInfo::MakeN32Premul(width,height));
    this->width = width;
    this->height = height;
    if (canvas != nullptr) {
        delete canvas;
        canvas = nullptr;
    }
    canvas = new SkCanvas(tmp);
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

jlong SkiaInstance::getBitmap() {
    if (canvas == nullptr) return 0;
    canvas->getSurface()->readPixels(tmp, 0, 0);
    void * pixelData = tmp.getPixels();
    return 0; //static_cast<jlong>(tmp.getPixels());
}

void SkiaInstance::drawText(const char *text, int index, int count, float x, float y, SkPaint & paint,
                            SkFont & font) {
    sk_sp<SkTextBlob> blob1 = SkTextBlob::MakeFromString(text, font);
    canvas->drawTextBlob(blob1.get(), x, y, paint);
}
