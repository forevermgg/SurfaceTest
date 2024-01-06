#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include <cassert>

extern "C" JNIEXPORT jstring JNICALL
Java_com_mgg_surface_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C" {

JNIEXPORT jint JNICALL
Java_com_mgg_surface_MainActivity_nativeGetSurfaceFormat(JNIEnv *env, jobject clazz,
                                                           jobject jsurface) {
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, jsurface);
    assert(nativeWindow != nullptr);
    int32_t format = ANativeWindow_getFormat(nativeWindow);
    ANativeWindow_release(nativeWindow);
    return format;
}

}  // extern "C"