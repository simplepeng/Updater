package com.simplepeng.updater;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.simplepeng.updaterlibrary.ProgressListener;
import com.simplepeng.updaterlibrary.Updater;

public class MainActivity extends AppCompatActivity {

    private final String url = "http://img.yqsapp.com/upload/1/appdownload/2016/11/MoneyTree5.2.1.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Updater updater = new Updater.Builder(MainActivity.this)
                        .setDownloadUrl(url)
                        .setApkName("test.apk")
                        .setAppName("updater")
                        .setApkPath(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .start();

                updater.addProgressListener(new ProgressListener() {
                    @Override
                    public void onProgressChange(int maxBytes, int curBytes, int progress) {
                        progressBar.setProgress((int) progress);
                    }
                });
            }
        });
    }
}
