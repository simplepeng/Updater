package com.simplepeng.updater;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simplepeng.updaterlibrary.LogUtils;
import com.simplepeng.updaterlibrary.ProgressListener;
import com.simplepeng.updaterlibrary.Updater;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{


    private final String url = "http://113.200.98.173/imtt.dd.qq.com/16891/B974CB68F01C2BD7007CB" +
        "0FA14F9BBC7.apk?mkey=587f32ca98f637cf&f=d287&c=0&fsname=com.daimajia.gold_3.9.5_128.ap" +
        "k&hsr=4d5s&p=.apk";

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

                startUpdater(progressBar, tv_total, tv_current);

            }
        });
    }

    private void startUpdater(final ProgressBar progressBar, final TextView tv_total,
                              final TextView tv_current) {
        updater = new Updater.Builder(MainActivity.this)
                .setDownloadUrl(url)
                .setApkName("test.apk")
//                        .setApkDir("test")
//                        .setApkPath(Environment.getExternalStorageDirectory().getAbsolutePath())
                .setNotificationTitle("updater")
//                .hideNotification()
                .debug()
                .start();

//        updater.registerDownloadReceiver();

        updater.addProgressListener(new ProgressListener() {
            @Override
            public void onProgressChange(long totalBytes, long curBytes, int progress) {
                progressBar.setProgress(progress);
                tv_total.setText("totalBytes-->>" + totalBytes);
                tv_current.setText("curBytes-->>" + curBytes);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updater.unRegisterDownloadReceiver();
        LogUtils.debug("onDestroy");
    }

    /**
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (updater != null) {
            updater.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    /**
     * 请求权限通过
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtils.debug("onPermissionsGranted");
        if (updater != null) {
            updater.onPermissionsGranted(requestCode,perms);
        }
    }

    /**
     * 请求权限拒绝
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtils.debug("onPermissionsDenied");
        if (updater != null) {
            updater.onPermissionsDenied(requestCode,perms);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
