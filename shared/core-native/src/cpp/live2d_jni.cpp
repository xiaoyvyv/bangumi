#include <jni.h>
#include <string>
#include <vector>
#include "live2d_renderer.h"

#if defined(ANDROID) || defined(__ANDROID__)
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#endif

extern "C" {



JNIEXPORT jlong JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeCreate(JNIEnv *env, jobject thiz) {
    return reinterpret_cast<jlong>(live2d_create());
}

JNIEXPORT void JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeDestroy(JNIEnv *env, jobject thiz, jlong handle) {
    live2d_destroy(reinterpret_cast<Live2DHandle>(handle));
}

JNIEXPORT jboolean JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeLoadModel(
        JNIEnv *env, jobject thiz, jlong handle, jstring model_dir, jstring model_json_name) {
    if (!handle || !model_dir || !model_json_name) return JNI_FALSE;
    const char *c_model_dir = env->GetStringUTFChars(model_dir, nullptr);
    const char *c_model_json_name = env->GetStringUTFChars(model_json_name, nullptr);

    bool result = live2d_load_model(reinterpret_cast<Live2DHandle>(handle), c_model_dir, c_model_json_name);

    env->ReleaseStringUTFChars(model_dir, c_model_dir);
    env->ReleaseStringUTFChars(model_json_name, c_model_json_name);

    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeLoadModelFromZip(
        JNIEnv *env, jobject thiz, jlong handle, jstring zip_file_path, jstring work_dir, jstring model_name) {
    if (!handle || !zip_file_path || !work_dir || !model_name) return JNI_FALSE;
    const char *c_zip_path = env->GetStringUTFChars(zip_file_path, nullptr);
    const char *c_work_dir = env->GetStringUTFChars(work_dir, nullptr);
    const char *c_model_name = env->GetStringUTFChars(model_name, nullptr);

    bool result = live2d_load_model_from_zip(reinterpret_cast<Live2DHandle>(handle), c_zip_path, c_work_dir, c_model_name);

    env->ReleaseStringUTFChars(zip_file_path, c_zip_path);
    env->ReleaseStringUTFChars(work_dir, c_work_dir);
    env->ReleaseStringUTFChars(model_name, c_model_name);

    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeSetMotion(
        JNIEnv *env, jobject thiz, jlong handle, jstring group, jint index) {
    if (!handle || !group) return;
    const char *c_group = env->GetStringUTFChars(group, nullptr);
    live2d_set_motion(reinterpret_cast<Live2DHandle>(handle), c_group, index);
    env->ReleaseStringUTFChars(group, c_group);
}

JNIEXPORT void JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeSetExpression(
        JNIEnv *env, jobject thiz, jlong handle, jstring expression_id) {
    if (!handle || !expression_id) return;
    const char *c_expr = env->GetStringUTFChars(expression_id, nullptr);
    live2d_set_expression(reinterpret_cast<Live2DHandle>(handle), c_expr);
    env->ReleaseStringUTFChars(expression_id, c_expr);
}

JNIEXPORT jobjectArray JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeGetMotions(
        JNIEnv *env, jobject thiz, jlong handle) {
    int count = live2d_get_motion_count(reinterpret_cast<Live2DHandle>(handle));
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray result = env->NewObjectArray(count, stringClass, nullptr);
    for (int i = 0; i < count; i++) {
        const char *str = live2d_get_motion_group_at(reinterpret_cast<Live2DHandle>(handle), i);
        env->SetObjectArrayElement(result, i, env->NewStringUTF(str));
    }
    return result;
}

JNIEXPORT jobjectArray JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeGetExpressions(
        JNIEnv *env, jobject thiz, jlong handle) {
    int count = live2d_get_expression_count(reinterpret_cast<Live2DHandle>(handle));
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray result = env->NewObjectArray(count, stringClass, nullptr);
    for (int i = 0; i < count; i++) {
        const char *str = live2d_get_expression_id_at(reinterpret_cast<Live2DHandle>(handle), i);
        env->SetObjectArrayElement(result, i, env->NewStringUTF(str));
    }
    return result;
}

JNIEXPORT void JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeOnSurfaceCreated(
        JNIEnv *env, jobject thiz, jlong handle) {
    live2d_on_surface_created(reinterpret_cast<Live2DHandle>(handle));
}

JNIEXPORT void JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeOnSurfaceChanged(
        JNIEnv *env, jobject thiz, jlong handle, jint width, jint height) {
    live2d_on_surface_changed(reinterpret_cast<Live2DHandle>(handle), width, height);
}

JNIEXPORT void JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeOnDrawFrame(
        JNIEnv *env, jobject thiz, jlong handle) {
    live2d_on_draw_frame(reinterpret_cast<Live2DHandle>(handle));
}

JNIEXPORT void JNICALL
Java_com_xiaoyv_bangumi_shared_component_Live2DNativeBridge_nativeOnTouch(
        JNIEnv *env, jobject thiz, jlong handle, jfloat x, jfloat y, jint phase) {
    live2d_on_touch(reinterpret_cast<Live2DHandle>(handle), x, y, phase);
}

}
