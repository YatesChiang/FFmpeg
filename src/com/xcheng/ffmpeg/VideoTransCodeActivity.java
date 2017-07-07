package com.xcheng.ffmpeg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.File;

public class VideoTransCodeActivity extends BaseTransCodeActivity {

    private static final String TAG = "VideoTransCodeActivity";
    private static final int REQUEST_VIDEO_FILE_CODE = 2;
    private String inputFile_path;

    private TextView mSelectFileTxT;
    private EditText mHourEditText;
    private EditText mMinuteEditText;
    private EditText mSecondEditText;
    private EditText mDurationEditText;
    private EditText mOutPutNameEditText;
    private EditText mFpsEditText;
    private Spinner mFormatSpinner;
    private EditText mWidthEditText;
    private EditText mHeightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_transcode);

        mSelectFileTxT = (TextView) findViewById(R.id.txt_select_file);
        mHourEditText = (EditText) findViewById(R.id.edit_hour);
        mMinuteEditText = (EditText) findViewById(R.id.edit_minute);
        mSecondEditText = (EditText) findViewById(R.id.edit_second);
        mDurationEditText = (EditText) findViewById(R.id.edit_duration);
        mOutPutNameEditText = (EditText) findViewById(R.id.edit_output_name);
        mFpsEditText = (EditText) findViewById(R.id.edit_fps);
        mFormatSpinner = (Spinner) findViewById(R.id.spinner_format);
        mWidthEditText = (EditText) findViewById(R.id.edit_width);
        mHeightEditText = (EditText) findViewById(R.id.edit_height);

        Button startBtn = (Button) findViewById(R.id.button_start);
        Button openStorageBtn = (Button) findViewById(R.id.button_open_storage);

        String[] styles = getResources().getStringArray(R.array.video_styles);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, styles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFormatSpinner.setAdapter(adapter);

        mSelectFileTxT.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){
                Intent intent = new Intent(VideoTransCodeActivity.this, FileListActivity.class);
                intent.putExtra("from_activity", "video");
                startActivityForResult(intent, REQUEST_VIDEO_FILE_CODE);
            }
        });

        mFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {

                String[] styles = getResources().getStringArray(R.array.video_styles);
                output_format =  styles[pos] ;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        openStorageBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){
                Intent intent = new Intent("com.xcheng.ffmpeg");
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.mediatek.filemanager", "com.mediatek.filemanager.FileManagerOperationActivity");
                intent.setComponent(cn);
                startActivity(intent);
            }
        });

        startBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){

                String toastString = commandExistEmpty();
                if (toastString != null) {
                    Toast.makeText(VideoTransCodeActivity.this, toastString, Toast.LENGTH_SHORT).show();
                    return;
                }
                runCommandAsync();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_VIDEO_FILE_CODE:
            if (resultCode == RESULT_OK) {
                String file_name = data.getStringExtra("result_name");
                inputFile_path = data.getStringExtra("result_path");
                outputFile_title = file_name;
                mSelectFileTxT.setText(file_name);
            }
            break;
        default:
            break;
        }
    }

    @Override
    protected String getCommand() {

        String cmd;
        String inputfile_Name = inputFile_path;
        String startTime = mHourEditText.getText().toString() + ":" + mMinuteEditText.getText().toString() + ":" + mSecondEditText.getText().toString();
        String duration = mDurationEditText.getText().toString();
        String outPutFileName = mOutPutNameEditText.getText().toString() + "_" + getSystemTime() + "." + output_format;
        String filePath = getFilePath("video", outPutFileName);
        String fps = mFpsEditText.getText().toString();
        String pictureSize ;
        String width = mWidthEditText.getText().toString();
        String height = mHeightEditText.getText().toString();
        pictureSize = "-s " + width + "*" + height + " ";
        if (TextUtils.isEmpty(duration)) {
            if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
                cmd = "ffmpeg" + " -r " + fps + " -i " + inputfile_Name + " -acodec copy " + filePath;
            } else {
                cmd = "ffmpeg" + " -r " + fps + " -i " + inputfile_Name + " -acodec copy " + pictureSize + filePath;
            }
        } else {
            if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
                cmd = "ffmpeg -ss " + startTime + " -t " + duration + " -r " + fps + " -i " + inputfile_Name + " -acodec copy " + filePath;
            } else {
                cmd = "ffmpeg -ss " + startTime + " -t " + duration + " -r " + fps + " -i " + inputfile_Name + " -acodec copy " + pictureSize + filePath;
            }
        }
        Log.d("cmd", cmd);
        outputFile_path = filePath;
        return cmd;
    }

    private String commandExistEmpty(){
        String outPutFileName = mOutPutNameEditText.getText().toString();
        if(TextUtils.isEmpty(outPutFileName)){
            return getResources().getString(R.string.toast_output_name_empty);
        }

        return null;
    }
}
