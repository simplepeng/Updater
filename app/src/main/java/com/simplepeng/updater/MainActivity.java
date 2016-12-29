package com.simplepeng.updater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simplepeng.updaterlibrary.LogUtils;
import com.simplepeng.updaterlibrary.ProgressListener;
import com.simplepeng.updaterlibrary.Updater;

public class MainActivity extends AppCompatActivity {

    private final String url = "https://dl.winudf.com/c/APK/3281/03992c2739ba4" +
            "c36.apk?_fn=QVBLUHVyZV92MS4yLjVfYXBrcHVyZS5jb20uYXBr&_p=Y29tLmFwa3B1cmUuYWVnb24%3D&as" +
            "=ba330e2a94a1f30a10b70b5058dc68075864c997&c" +
            "=1%7CTOOLS&k=6295a35413ef815b3e486f61080cd30a5866f87b";

    private Updater updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView tv_total = (TextView) findViewById(R.id.tv_total);
        final TextView tv_current = (TextView) findViewById(R.id.tv_current);
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updater = new Updater.Builder(getApplicationContext())
                        .setDownloadUrl(url)
                        .setApkName("test.apk")
//                        .setApkDir("test")
//                        .setApkPath(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .setNotificationTitle("updater")
                        .start();

                updater.registerDownloadReceiver();

                updater.addProgressListener(new ProgressListener() {
                    @Override
                    public void onProgressChange(long totalBytes, long curBytes, int progress) {
                        progressBar.setProgress(progress);
                        tv_total.setText("totalBytes-->>" + totalBytes);
                        tv_current.setText("curBytes-->>" + curBytes);
                    }
                });


            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updater.unRegisterDownloadReceiver();
        LogUtils.debug("onDestroy");
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
