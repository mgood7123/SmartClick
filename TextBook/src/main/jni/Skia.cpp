//
// Created by matthew good on 24/10/20.
//

#include <android/bitmap.h>
#include <include/core/SkFont.h>
#include <include/core/SkTextBlob.h>
#include "SkiaInstance.h"
#include "JniHelpers.h"

extern "C" JNIEXPORT jlong JNICALL
Java_smallville7123_textbook_Skia_createNativeInstance(JNIEnv* env, jobject thiz) {
    return reinterpret_cast<jlong>(new SkiaInstance());
}

extern "C" JNIEXPORT void JNICALL
Java_smallville7123_textbook_Skia_createCanvas(JNIEnv* env, jobject thiz,
        jlong native_skia_ptr, jint width, jint height
) {
    SkiaInstance::getInstance(native_skia_ptr).createCanvas(width, height);
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_smallville7123_textbook_Skia_getPixels(JNIEnv *env, jobject thiz, jlong native_skia_ptr) {
    jintArray pixels = SkiaInstance::getInstance(native_skia_ptr).getPixels(env);
    return pixels;
}

extern "C"
JNIEXPORT void JNICALL
Java_smallville7123_textbook_Skia_freePixels(JNIEnv *env, jobject thiz, jlong native_skia_ptr,
        jintArray pixels) {
    // TODO
//    JniHelpers::Arrays::createJniIntArray(env, length);
}

extern "C" JNIEXPORT void JNICALL
Java_smallville7123_textbook_Skia_drawText(JNIEnv* env, jobject thiz,
        jlong native_skia_ptr, jstring text, int index, int count, float x, float y,
        jlong paint, jlong font
) {
    size_t size;
    const char * nText = JniHelpers::Strings::newJniStringUTF(env, text, &size);
    switch (size) {
        case 1:
            switch (nText[0]) {
                case '\n':
                    JniHelpers::Strings::deleteJniStringUTF(&nText);
                    return;
            }
    }

    auto skia = SkiaInstance::getInstance(native_skia_ptr);
    SkPaint & p = SkiaInstance::getPaint(paint);
    SkFont & f = SkiaInstance::getFont(font);
    skia.drawText(nText, index, count, x, y, p, f);
    JniHelpers::Strings::deleteJniStringUTF(&nText);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_smallville7123_textbook_Skia_constructPaint(JNIEnv *env, jobject thiz, jint alpha, jboolean isAntiAlias, jint textColor,
                                                 jint style) {
    SkPaint * paint = new SkPaint;
    paint->setAntiAlias(isAntiAlias);
    paint->setColor(textColor);
    switch (style) {
        case SkPaint::kFill_Style:
            paint->setStyle(SkPaint::kFill_Style);
            break;
        case SkPaint::kStroke_Style:
            paint->setStyle(SkPaint::kStroke_Style);
        case SkPaint::kStrokeAndFill_Style:
            paint->setStyle(SkPaint::kStrokeAndFill_Style);
            break;
    }
    return reinterpret_cast<jlong>(paint);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_smallville7123_textbook_Skia_constructFont(JNIEnv *env, jobject thiz, jfloat text_size,
                                                jfloat text_scale_x, jfloat text_skew_x) {
    SkFont * font = new SkFont(nullptr, text_size, text_scale_x, text_skew_x);
    return reinterpret_cast<jlong>(font);
}

extern "C"
JNIEXPORT jint JNICALL
Java_smallville7123_textbook_Skia_getWidth(JNIEnv *env, jobject thiz, jlong native_skia_ptr) {
    return SkiaInstance::getInstance(native_skia_ptr).width;
}

extern "C"
JNIEXPORT jint JNICALL
Java_smallville7123_textbook_Skia_getHeight(JNIEnv *env, jobject thiz, jlong native_skia_ptr) {
    return SkiaInstance::getInstance(native_skia_ptr).height;
}

extern "C"
JNIEXPORT void JNICALL
Java_smallville7123_textbook_Skia_clear(JNIEnv *env, jobject thiz, jlong native_skia_ptr,
        jint color) {
    return SkiaInstance::getInstance(native_skia_ptr).clear(color);
}
extern "C"
JNIEXPORT jint JNICALL
Java_smallville7123_textbook_Skia_getStride(JNIEnv *env, jobject thiz, jlong native_skia_ptr) {
    return SkiaInstance::getInstance(native_skia_ptr).stride;
}