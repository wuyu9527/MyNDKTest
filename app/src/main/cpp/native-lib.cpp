#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>
#include "openssl/md5.h"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, "leenjewel",__VA_ARGS__)

extern "C"{
JNIEXPORT jstring JNICALL
Java_com_whx_myndk_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

//用于加密解密的密码
char password[] = "I AM MI MA";

JNIEXPORT jstring JNICALL
Java_com_whx_myndk_MainActivity_stringFromMD5(
        JNIEnv *env,
        jobject thiz/* this */,
        jstring srcjStr) {

    const char *unicodeChar = env->GetStringUTFChars(srcjStr, NULL);
    size_t unicodeCharLength = env->GetStringLength(srcjStr);

    LOGD("stringFromMD5( %s ) = %d", unicodeChar, unicodeCharLength);

    unsigned char md[MD5_DIGEST_LENGTH];
    int i;
    char buf[33] = {'\0'};
    MD5((unsigned char *) unicodeChar, unicodeCharLength, (unsigned char *) &md);
    for (i = 0; i < 16; i++) {
        sprintf(&buf[i * 2], "%02x", md[i]);
    }
    env->ReleaseStringUTFChars(srcjStr, unicodeChar);
    return env->NewStringUTF(buf);
}

}



