package com.xcheng.ffmpeg;

import android.app.ActionBar;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private Context context;

    private Button videoTransCode;
    private Button audioTransCode;
    private Button pictureTransCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        requestPermission();
        videoTransCode = (Button)findViewById(R.id.video_transcode_button);
        audioTransCode = (Button)findViewById(R.id.audio_transcode_button);
        pictureTransCode = (Button)findViewById(R.id.picture_transcode_button);
        videoTransCode.setOnClickListener(this);
        audioTransCode.setOnClickListener(this);
        pictureTransCode.setOnClickListener(this);

        ActionBar mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }
    }

    private void requestPermission() {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults != null && grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context,getString(R.string.denied_required_permission),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
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

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.video_transcode_button:
                Intent video_intent = new Intent(MainActivity.this, VideoTransCodeActivity.class);
                startActivity(video_intent);
                break;
            case R.id.audio_transcode_button:
                Intent audio_intent = new Intent(MainActivity.this, AudioTransCodeActivity.class);
                startActivity(audio_intent);
                break;
            case R.id.picture_transcode_button:
                Intent picture_intent = new Intent(MainActivity.this, PictureTransCodeActivity.class);
                startActivity(picture_intent);
                break;
            default:
                break;
        }
    }
}