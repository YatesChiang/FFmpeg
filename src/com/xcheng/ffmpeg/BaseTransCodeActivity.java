package com.xcheng.ffmpeg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import android.widget.Toast;

import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import java.io.File;

public class BaseTransCodeActivity extends Activity {

    private final String TAG = "BaseTransCodeActivity";
    private AsyncTask<Void, Void, String> mRunCommandAsyncTask;
    private static final int EVENT_SHOW_PROCESS_DIALOG = 0;
    private static final int EVENT_DISMISS_PROCESS_DIALOG = 1;
    private static ProgressDialog mProgressDialog;
    private WaitingDialogHanlder mProgressDialogHandler;
    private static Context context;
    protected String output_format;
    protected String outputFile_title;
    protected String outputFile_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Waiting...");
        context = getApplicationContext();
        mProgressDialogHandler = new WaitingDialogHanlder();
    }

    public String getSystemTime() {
        Date date = new Date();
        String time = new SimpleDateFormat("hh_mm_ss").format(date);
        return time;
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item); 
    }

    protected String getCommand() {
        return null;
    }

    private void prepare() {
        mRunCommandAsyncTask = null;
    }

    protected void runCommandAsync() {
        prepare();
        mRunCommandAsyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Log.d(TAG, "doInBackground");
                String result = null;
                String cmd = getCommand();
                Log.d(TAG,cmd);
                if (cmd != null) {
                    result = executeCommand(cmd);
                }
                if(output_format.equals("gif")) {
                    Log.d(TAG, "format=" + output_format + ", titile=" + outputFile_title + ", path=" + outputFile_path);
                    saveImageToDatabase(outputFile_title, outputFile_path);
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(TAG, "onPostExecute result = " + result);
                mProgressDialogHandler.removeMessages(EVENT_SHOW_PROCESS_DIALOG);
                mProgressDialogHandler.sendEmptyMessage(EVENT_DISMISS_PROCESS_DIALOG);
            }
        };

        mRunCommandAsyncTask.execute();
        mProgressDialogHandler.sendEmptyMessageDelayed(EVENT_SHOW_PROCESS_DIALOG, 300);
    }

    private String executeCommand(String command) {
        String[] argv = command.split(" ");
        Integer argc = argv.length;
        return FFmpegNativeHelper.ffmpeg_run(argc, argv);
    }

    protected String getFilePath(String type, String fileName) {
        File resultDir = new File("/storage/emulated/0/FFmpeg");
        if (!resultDir.exists()) {
            resultDir.mkdir();
        }
        
        File saveDir = new File("/storage/emulated/0/FFmpeg/" + type);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        return "/storage/emulated/0/FFmpeg/" + type + "/" + fileName;
    }

    public void saveImageToDatabase(String title, String path) {
        Log.i(TAG, "[saveImageToDatabase]...");
        // Insert into MediaStore.
        ContentValues values = new ContentValues(2);
        values.put(ImageColumns.TITLE, title);
        values.put(ImageColumns.DATA, path);
        try {
            ContentResolver resolver = getContentResolver();
            Uri uri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, uri));
                // Keep compatibility
                sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
            }
            Log.i(TAG, "[saveImageToDatabase]mUri = " + uri);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "[saveImageToDatabase]Failed to write MediaStore,IllegalArgumentException:", e);
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "[saveImageToDatabase]Failed to write MediaStore," + "UnsupportedOperationException:",e);
        }
    }

    private static class WaitingDialogHanlder extends Handler {

        private static String transcodeComplete = context.getResources().getString(R.string.toast_transcode_complete);

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_SHOW_PROCESS_DIALOG:
                    mProgressDialog.show();
                    break;
                case EVENT_DISMISS_PROCESS_DIALOG:
                    mProgressDialog.dismiss();
                    Toast.makeText(context, transcodeComplete, Toast.LENGTH_SHORT).show();
                    break;
                default: return;
            }
        }
    }
}