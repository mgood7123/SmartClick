//
// Created by matthew good on 24/10/20.
//

#include <include/core/SkFont.h>
#include <include/core/SkTextBlob.h>
#include "SkiaInstance.h"
#include "JniHelpers.h"

void SkiaInstance::createCanvas(int width, int height) {
    bitmap.allocPixels(SkImageInfo::MakeN32Premul(width,height));
    this->width = width;
    this->height = height;
    if (canvas != nullptr) {
        delete canvas;
        canvas = nullptr;
    }
    canvas = new SkCanvas(bitmap);
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

void * SkiaInstance::getPixels() {
    return bitmap.getPixels();
}

void SkiaInstance::drawText(const char *text, int index, int count, float x, float y, SkPaint & paint,
                            SkFont & font) {
    sk_sp<SkTextBlob> blob1 = SkTextBlob::MakeFromString(text, font);
    canvas->drawTextBlob(blob1.get(), x, y, paint);
}

bool SkiaInstance::readPixels() {
    return canvas == nullptr ? false : canvas->readPixels(bitmap, 0, 0);
}

SkBitmap &SkiaInstance::getBitmap() {
    return bitmap;
}

size_t SkiaInstance::getPixelDataLength() {
    return bitmap.computeByteSize();
}

void SkiaInstance::clear(SkColor color) {
    canvas->clear(color);
}

int SkiaInstance::getStride() {
    return bitmap.rowBytesAsPixels();
}

jintArray SkiaInstance::getPixels(JNIEnv *env) {
    auto sRGB = SkColorSpace::MakeSRGB();
    SkImageInfo dstInfo = SkImageInfo::Make(
            width, height, kBGRA_8888_SkColorType, kUnpremul_SkAlphaType, sRGB);
    size_t dstRowBytes = dstInfo.minRowBytes();
    auto length = dstInfo.computeMinByteSize();

    uint32_t * dst = new SkColor[length];
    bitmap.readPixels(dstInfo, dst, dstRowBytes, 0, 0);

    jintArray array = env->NewIntArray(length);
    env->SetIntArrayRegion(array, 0, length, reinterpret_cast<const jint *>(dst));
    delete[] dst;
    return array;
}
