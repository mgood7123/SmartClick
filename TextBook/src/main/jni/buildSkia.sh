export ANDROID_SDK_ROOT="$HOME/Android/Sdk-Mac"
export ANDROID_NDK_ROOT="$HOME/Android/Sdk-Mac/ndk/21.3.6528147"

cd skia

bin/gn gen out/armeabi-v7a --args="ndk=\"$ANDROID_NDK_ROOT\" target_cpu=\"arm\""
../depot_tools/ninja -C out/armeabi-v7a

bin/gn gen out/arm64-v8a     --args="ndk=\"$ANDROID_NDK_ROOT\" target_cpu=\"arm64\""
../depot_tools/ninja   -C out/arm64-v8a

bin/gn gen out/x64    --args="ndk=\"$ANDROID_NDK_ROOT\" target_cpu=\"x64\""
../depot_tools/ninja         -C out/x64

bin/gn gen out/x86_64    --args="ndk=\"$ANDROID_NDK_ROOT\" target_cpu=\"x86_64\""
../depot_tools/ninja         -C out/x86_64