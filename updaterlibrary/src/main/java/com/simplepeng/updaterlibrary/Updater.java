package com.simplepeng.updaterlibrary;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import java.io.File;

/**
 * Created by simple on 16/12/19.
 */

public class Updater {

    private String apkName;
    private String apkPath;
    private String apkDirName;
    private String appName;
    private String downloadUrl;
    private Context context;
    private DownloadManager downloadManager;
    private long mTaskId;
    private String apkAbsPath;
    private boolean hideNotification = false;
    private ProgressListener mProgressListener;

    public Updater(Context context) {
        this.context = context;
    }

    public void download() {
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        if (TextUtils.isEmpty(downloadUrl)) {
            throw new NullPointerException("downloadUrl must not be null");
        }
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        //获取一个下载请求
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        //设置wifi，流量都可以下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager
                .Request.NETWORK_WIFI);

        //漫游网络是否可以下载
        request.setAllowedOverRoaming(false);

        //设置文件类型，可以在下载结束后自动打开该文件
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
//                .getFileExtensionFromUrl(downloadUrl));
//        request.setMimeType("application/vnd.android.package-archive");

        //在通知栏中显示标题，默认就是显示的
        request.setTitle(TextUtils.isEmpty(appName) ? apkName : appName);

        //设置隐藏通知栏下载
        request.setNotificationVisibility(hideNotification ? View.GONE : View.VISIBLE);
        //设置下载路径
        if (TextUtils.isEmpty(apkPath) && TextUtils.isEmpty(apkDirName)) {
//            throw new NullPointerException("path or dir must has one path");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);
//            apkAbsPath = ;
        } else if (!TextUtils.isEmpty(apkDirName)) {
            apkAbsPath = apkDirName;
            request.setDestinationInExternalPublicDir(apkAbsPath, apkName);
        } else {
            apkAbsPath = apkPath + File.separator + apkName;
            LogUtils.debug(apkAbsPath);
            request.setDestinationUri(Uri.fromFile(new File(apkAbsPath)));
        }
        //将下载请求加入下载队列
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等
        mTaskId = downloadManager.enqueue(request);

//
//        //注册广播接收者，监听下载状态
//        activity.registerReceiver(receiver,
//                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void addProgressListener(ProgressListener progressListener) {
        this.mProgressListener = progressListener;
        context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/"),
                true, new DownloadObserver(handler, downloadManager, mTaskId));
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle data = msg.getData();
            int progress = data.getInt("progress");
            LogUtils.debug("data----progress======" + progress);
            if (mProgressListener != null) {
                mProgressListener.onProgressChange(0,0,progress);
            }
            return false;
        }
    });

    public static class Builder {

        private Updater mUpdater;

        public Builder(Context context) {
            mUpdater = new Updater(context);
        }

        public Builder setApkName(String apkName) {
            mUpdater.apkName = apkName;
            return this;
        }

        public Builder setApkPath(String apkPath) {
            mUpdater.apkPath = apkPath;
            return this;
        }

        public Builder setApkDir(String dirName) {
            mUpdater.apkDirName = dirName;
            return this;
        }

        public Builder setDownloadUrl(String downloadUrl) {
            mUpdater.downloadUrl = downloadUrl;
            return this;
        }

        public Builder setAppName(String appName) {
            mUpdater.appName = appName;
            return this;
        }

        public Builder hideNotification(){
            mUpdater.hideNotification = true;
            return this;
        }

        public Updater start() {
            mUpdater.download();
            return mUpdater;
        }

    }

}
