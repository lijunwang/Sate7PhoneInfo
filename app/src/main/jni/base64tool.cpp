//
// Created by zxwei on 2019/9/9.
//
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include "get_hard_sate7phoneinfo_util_Base64Tool.h"
#include "android/log.h"

#ifdef __cplusplus
extern "C" {
#endif
static const char *TAG = "Base64Tool";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
static const unsigned char base64_table[65] =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

//将char类型转换成jstring类型
jstring CStr2Jstring(JNIEnv *env, const char *str) {
    jsize len = strlen(str);
    // 定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");
    //设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("GB2312");
    // 获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    // 建立byte数组
    jbyteArray bytes = (env)->NewByteArray(len);
    // 将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, len, (jbyte *) str);
    //将byte数组转换为java String,并输出
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

//将jstring类型转换成char类型
char *Jstring2CStr(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = (env)->FindClass("java/lang/String");
    jstring strencode = (env)->NewStringUTF("GB2312");
    jmethodID mid = (env)->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) (env)->CallObjectMethod(jstr, mid, strencode);
    jsize alen = (env)->GetArrayLength(barr);
    jbyte *ba = (env)->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1); //new char[alen+1];
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (env)->ReleaseByteArrayElements(barr, ba, 0);

    return rtn;
}
char *ConvertJByteaArrayToChars(JNIEnv *env, jbyteArray bytearray) {
    char *chars = NULL;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(bytearray, 0);
    int chars_len = env->GetArrayLength(bytearray);
    chars = new char[chars_len + 1];
    memset(chars, 0, chars_len + 1);
    memcpy(chars, bytes, chars_len);
    chars[chars_len] = 0;

    env->ReleaseByteArrayElements(bytearray, bytes, 0);

    return chars;
}

jbyteArray util_char_to_jbyteArray(JNIEnv *env, char *data, int length) {
    jbyte *by = (jbyte *) data;
    jbyteArray jarray = env->NewByteArray(length);
    env->SetByteArrayRegion(jarray, 0, length, by);
    return jarray;
}


JNIEXPORT jstring JNICALL Java_get_hard_sate7phoneinfo_util_Base64Tool_nativeEncode
        (JNIEnv *env, jobject jobj, jbyteArray srcData, jint len) {
    unsigned char *out, *pos;
    const unsigned char *end, *in;
    size_t olen;
    int line_len;
    //const unsigned char *src  = (unsigned char*)env->GetByteArrayElements(srcData, 0);
    const unsigned char *src = (unsigned char *) ConvertJByteaArrayToChars(env, srcData);
    LOGD("===base64_encode=== %s", src);

    olen = len * 4 / 3 + 4; /* 3-byte blocks to 4-byte */
    olen += olen / 72; /* line feeds */
    olen++; /* nul termination */
    if (olen < len)
        return NULL; /* integer overflow */
    out = (unsigned char *) malloc(olen);
    if (out == NULL)
        return NULL;

    end = src + len;
    in = src;
    pos = out;
    line_len = 0;
    while (end - in >= 3) {
        *pos++ = base64_table[in[0] >> 2];
        *pos++ = base64_table[((in[0] & 0x03) << 4) | (in[1] >> 4)];
        *pos++ = base64_table[((in[1] & 0x0f) << 2) | (in[2] >> 6)];
        *pos++ = base64_table[in[2] & 0x3f];
        in += 3;
        line_len += 4;
        if (line_len >= 72) {
            *pos++ = '\n';
            line_len = 0;
        }
    }

    if (end - in) {
        *pos++ = base64_table[in[0] >> 2];
        if (end - in == 1) {
            *pos++ = base64_table[(in[0] & 0x03) << 4];
            *pos++ = '=';
        } else {
            *pos++ = base64_table[((in[0] & 0x03) << 4) |
                                  (in[1] >> 4)];
            *pos++ = base64_table[(in[1] & 0x0f) << 2];
        }
        *pos++ = '=';
        line_len += 4;
    }

    if (line_len)
        *pos++ = '\n';

    *pos = '\0';
    delete src;
    return CStr2Jstring(env, (char *) out);

}

JNIEXPORT jbyteArray JNICALL Java_get_hard_sate7phoneinfo_util_Base64Tool_nativeDecode
        (JNIEnv *env, jobject jobj, jstring decodeData, jint len) {

    unsigned char dtable[256], *out, *pos, block[4], tmp;
    size_t i, count, olen;
    size_t out_len = 0;
    int pad = 0;
    jboolean iscopy;
    const char *srcD = (env)->GetStringUTFChars(decodeData, NULL);
    LOGD("base64_decode %s", srcD);
    const unsigned char *src = (unsigned char *) srcD;
    memset(dtable, 0x80, 256);
    for (i = 0; i < sizeof(base64_table) - 1; i++)
        dtable[base64_table[i]] = (unsigned char) i;
    dtable['='] = 0;

    count = 0;
    for (i = 0; i < len; i++) {
        if (dtable[src[i]] != 0x80)
            count++;
    }

    if (count == 0 || count % 4)
        return NULL;

    olen = count / 4 * 3;
    pos = out = (unsigned char *) malloc(olen);
    if (out == NULL)
        return NULL;

    count = 0;
    for (i = 0; i < len; i++) {
        tmp = dtable[src[i]];
        if (tmp == 0x80)
            continue;

        if (src[i] == '=')
            pad++;
        block[count] = tmp;
        count++;
        if (count == 4) {
            *pos++ = (block[0] << 2) | (block[1] >> 4);
            *pos++ = (block[1] << 4) | (block[2] >> 2);
            *pos++ = (block[2] << 6) | block[3];
            count = 0;
            if (pad) {
                if (pad == 1)
                    pos--;
                else if (pad == 2)
                    pos -= 2;
                else {
                    /* Invalid padding */
                    free(out);
                    return NULL;
                }
                break;
            }
        }
    }
    out_len = pos - out;
    (env)->ReleaseStringUTFChars(decodeData, srcD);
    return util_char_to_jbyteArray(env, (char *) out, out_len);

}
#ifdef __cplusplus
}
#endif