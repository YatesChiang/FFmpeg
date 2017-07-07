package com.xcheng.ffmpeg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
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

public class PictureTransCodeActivity extends BaseTransCodeActivity {

    private static final int REQUEST_IMAGES_FILE_CODE = 2;
    private String file_path;
    private TextView mSelectFileTxT;
    private EditText mDurationEditText;
    private EditText mOutPutNameEditText;
    private Spinner mFormatSpinner;
    private EditText mWidthEditText;
    private EditText mHeightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_transcode);

        mSelectFileTxT = (TextView) findViewById(R.id.txt_select_file);
        mOutPutNameEditText = (EditText) findViewById(R.id.edit_output_name);
        mFormatSpinner = (Spinner) findViewById(R.id.spinner_format);
        mWidthEditText = (EditText) findViewById(R.id.edit_width);
        mHeightEditText = (EditText) findViewById(R.id.edit_height);

        Button startBtn = (Button) findViewById(R.id.button_start);
        Button openStorageBtn = (Button) findViewById(R.id.button_open_storage);

        String[] styles = getResources().getStringArray(R.array.picture_styles);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_item, styles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFormatSpinner.setAdapter(adapter);

        mSelectFileTxT.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){
                Intent intent = new Intent(PictureTransCodeActivity.this, FileListActivity.class);
                intent.putExtra("from_activity", "images");
                startActivityForResult(intent, REQUEST_IMAGES_FILE_CODE);
            }
        });

        mFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {

                String[] styles = getResources().getStringArray(R.array.picture_styles);
                output_format =  styles[pos] ;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        openStorageBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0){
                Intent intent = new Intent(Intent.ACTION_MAIN);
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
                    Toast.makeText(PictureTransCodeActivity.this, toastString, Toast.LENGTH_SHORT).show();
                    return;
                }
                runCommandAsync();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_IMAGES_FILE_CODE:
            if (resultCode == RESULT_OK) {
                String file_name = data.getStringExtra("result_name");
                file_path = data.getStringExtra("result_path");
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
        String inputfile_Name = file_path;
        String outPutFileName = mOutPutNameEditText.getText().toString() + "_" + getSystemTime() + "." + output_format;
        String filePath = getFilePath("image", outPutFileName);
        String pictureSize ;
        String width = mWidthEditText.getText().toString();
        String height = mHeightEditText.getText().toString();
        if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)){
            cmd = "ffmpeg" + " -i " + inputfile_Name + " " + filePath;
        } else {
            pictureSize = " -s " + width + "*" + height + " ";
            cmd = "ffmpeg" + " -i " + inputfile_Name + pictureSize + filePath;
        }
        Log.d("cmd", cmd);
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
