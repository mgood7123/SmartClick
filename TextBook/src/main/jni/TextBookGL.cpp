//
// Created by matthew good on 25/10/20.
//

#include "SkiaInstance.h"
#include "JniHelpers.h"
#include "log.h"
#include <thread>

#include <android/native_window.h>
#include <android/native_window_jni.h>

#include <EGL/egl.h>
#include <GLES/gl.h>

static ANativeWindow *window = 0;

class EGL {
public:
    int init_EGL = false;
    bool
            init_eglGetDisplay = false,
            init_eglInitialize = false,
            init_eglChooseConfig = false,
            init_eglCreateWindowSurface = false,
            init_eglCreatePbufferSurface = false,
            init_eglCreateContext = false,
            init_eglMakeCurrent = false,
            init_debug = false;
    const GLint
            *configuration_attributes = nullptr,
            *context_attributes = nullptr,
            *surface_attributes = nullptr;
    EGLint
            eglMajVers = 0,
            eglMinVers = 0,
            number_of_configurations = 0;
    EGLNativeDisplayType display_id = EGL_DEFAULT_DISPLAY;
    EGLDisplay display = EGL_NO_DISPLAY;
    EGLConfig configuration = 0;
    EGLContext
            context = EGL_NO_CONTEXT,
            shared_context = EGL_NO_CONTEXT;

    int EGL_CONTEXT_CLIENT_VERSION_ = 3;

    EGLSurface surface = EGL_NO_SURFACE;
    // previously: ANativeWindow *native_window = nullptr;
    EGLNativeWindowType native_window = 0;
    GLint
            width = 0,
            height = 0,
            surface_width = 0,
            surface_height = 0;

    void basicInit() {
        const EGLint config_attrs[] = {EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL_NONE};
        configuration_attributes = config_attrs;

        const EGLint surface_attrs[] = {EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT, EGL_BLUE_SIZE, 8,
                                  EGL_GREEN_SIZE, 8, EGL_RED_SIZE, 8, EGL_ALPHA_SIZE, 8,
                                  EGL_DEPTH_SIZE, 16, EGL_NONE};
        surface_attributes = surface_attrs;

        // do no error checking

        LOG_INFO("Initializing");

        eglBindAPI(EGL_OPENGL_ES_API);

        LOG_INFO("Initializing display");
        display = eglGetDisplay(display_id);
        init_eglGetDisplay = true;

        eglInitialize(display, &eglMajVers, &eglMinVers);
        init_eglInitialize = true;
        LOG_INFO("Initialized display");

        LOG_INFO("Initializing configuration");

        const EGLint context_attrs[] = {EGL_CONTEXT_CLIENT_VERSION, EGL_CONTEXT_CLIENT_VERSION_,
                                        EGL_NONE};
        context_attributes = context_attrs;

        eglChooseConfig(display, configuration_attributes, &configuration, 1,
                        &number_of_configurations);
        init_eglChooseConfig = true;
        LOG_INFO("Initialized configuration");

        LOG_INFO("Initializing surface");
        surface = eglCreateWindowSurface(display, configuration, native_window,
                                             nullptr);
        init_eglCreateWindowSurface = true;
        LOG_INFO("Initialized surface");

        LOG_INFO("Initializing context");
        context = eglCreateContext(display, configuration, shared_context,
                                       context_attributes);
        init_eglCreateContext = true;
        LOG_INFO("Initialized context");

        LOG_INFO("Switching to context");
        eglMakeCurrent(display, surface, surface, context);
        init_eglMakeCurrent = true;
        LOG_INFO("Switched to context");

        LOG_INFO("Obtaining surface width and height");
        eglQuerySurface(display, surface, EGL_WIDTH, &surface_width);
        eglQuerySurface(display, surface, EGL_HEIGHT, &surface_height);
        LOG_INFO("Obtained surface width and height");

        init_EGL = true;
        LOG_INFO("Initialized");
    };

    void basicDenit() {
        if (!init_EGL) return;

        LOG_INFO("Uninitializing");

        if (init_eglMakeCurrent) {
            LOG_INFO("Switching context to no context");
            eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
            init_eglMakeCurrent = false;
        }
        if (init_eglCreateContext) {
            LOG_INFO("Uninitializing context");
            eglDestroyContext(display, context);
            context = EGL_NO_CONTEXT;
            shared_context = EGL_NO_CONTEXT;
            init_eglCreateContext = false;
        }
        if (init_eglCreateWindowSurface || init_eglCreatePbufferSurface) {
            LOG_INFO("Uninitializing surface");
            eglDestroySurface(display, surface);
            surface = EGL_NO_SURFACE;
            init_eglCreateWindowSurface = false;
            init_eglCreatePbufferSurface = false;
        }

        if (init_eglInitialize) {
            LOG_INFO("Uninitializing display");
            eglTerminate(display);
            init_eglInitialize = false;
        }

        if (init_eglGetDisplay) {
            LOG_INFO("Setting display to no display");
            display = EGL_NO_DISPLAY;
            init_eglGetDisplay = false;
        }

        init_EGL = false;
        LOG_INFO("Uninitialized");
    }
};

std::atomic_bool running {false};

class TextBookGL {
public:
    class EGL egl;
    std::thread mainThread;

    void setWindow(ANativeWindow *pWindow) {
        egl.native_window = pWindow;
        if (egl.native_window == 0) {
            LOG_INFO("ending thread");
            running.store(false);
            mainThread.join();
            egl.basicDenit();
        } else {
            egl.basicInit();
            running.store(true);
            LOG_INFO("starting thread");
            mainThread = std::thread(&TextBookGL::main, this);
        }
    }

    static void main(TextBookGL * textBookGl) {
        LOG_INFO("thread started");
        while(running.load()) {
        }
        LOG_INFO("thread ended");
    }
};

TextBookGL textBookGL;

extern "C"
JNIEXPORT void JNICALL
Java_smallville7123_textbook_TextBookGL_nativeSetSurface(JNIEnv *env, jobject thiz,
                                                         jobject surface) {
    if (surface != 0) {
        window = ANativeWindow_fromSurface(env, surface);
        LOG_INFO("Got window %p", window);
        textBookGL.setWindow(window);
    } else {
        LOG_INFO("Releasing window");
        ANativeWindow_release(window);
        textBookGL.setWindow(0);
    }
}
