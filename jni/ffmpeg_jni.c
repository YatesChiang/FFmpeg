/**
 *
 * This software is a Transcoder in Android.
 * It is transplanted from ffmpeg.c command line tools.
 *
 */

#include <string.h>
#include <jni.h>
#include <ffmpeg.h>

#ifdef ANDROID
#include <jni.h>
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "(>_<)", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "(=_=)", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  LOGE("(>_<) " format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  LOGE("(^_^) " format "\n", ##__VA_ARGS__)
#endif


int main(int argc, char **argv);

//Output FFmpeg's av_log()
void log_callback(void *ptr, int level, const char* fmt, va_list vl)
{

    FILE *fp = fopen("/storage/emulated/0/av_log.txt","a+");
    if (fp)
    {
        vfprintf(fp, fmt, vl);
        fflush(fp);
        fclose(fp);
    }

}

JNIEXPORT jint JNICALL Java_com_xcheng_ffmpeg_FFmpegNativeHelper_ffmpeg_1run(JNIEnv * env, jobject thiz, jint cmdnum, jobjectArray cmdline)
{

    //FFmpeg av_log() callback
    av_log_set_callback(log_callback);

    int argc = cmdnum;
    char** argv = (char**)malloc(sizeof(char*)*argc);

    int i = 0;
    int ret = 1;

    for (i = 0; i < argc; i++)
    {
        argv[i] = (*env)->GetStringUTFChars(env,
                              (jstring)(*env)->GetObjectArrayElement(env, cmdline, i), NULL);
    }

    ret = main(argc, argv);

    for (i = 0; i < argc; i++){
        (*env)->ReleaseStringUTFChars(env,
                    (jstring)(*env)->GetObjectArrayElement(env, cmdline, i), argv[i]);
    }

    free(argv);

    if (ret == 0) {
        return (*env)->NewStringUTF(env, "SUCCESS");
    } else {
        return (*env)->NewStringUTF(env, "FAIL");
    }

}
