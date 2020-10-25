cmake_minimum_required(VERSION 3.4.1)

add_library(jniSkia SHARED
        JniHelpers.cpp
        Log.cpp
        Skia.cpp
        SkiaInstance.cpp
)

include_directories(skia)

set(SKIA ${CMAKE_CURRENT_SOURCE_DIR}/skia/out/${CMAKE_ANDROID_ARCH_ABI})

add_library(Skia STATIC IMPORTED)
set_target_properties(Skia PROPERTIES IMPORTED_LOCATION ${SKIA}/libskia.a)

target_link_libraries(jniSkia android log Skia EGL GLESv3 jnigraphics)
