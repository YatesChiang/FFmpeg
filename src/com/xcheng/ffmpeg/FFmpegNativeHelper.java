package com.xcheng.ffmpeg;

import android.util.Log;

public class FFmpegNativeHelper {

    static{
        System.loadLibrary("ffmpegjni");
    }

    // success 0, error 1
    public static native String ffmpeg_run(int argc, String[] argv);

}
