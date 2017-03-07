package com.simplepeng.updaterlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by simple on 16/12/19.
 * <p>
 * Updater
 */

public class Updater {

    private String apkFileName;
    private String apkFilePath;
    private String apkDirName;
    private String title;
    private String downloadUrl;
    private Activity context;
    private DownloadManager downloadManager;
    private long mTaskId;
    private boolean hideNotification = false;
    //    private ProgressListener mProgressListener;
    private boolean allowedOverRoaming = false;
    private DownloadReceiver downloadReceiver;
    private DownloadObserver downloadObserver;
    private boolean claerCache = false;
    //动态权限需要的
    private String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int RC_SDCARD = 123;


    private Updater(Activity context) {
        this.context = context;
    }

    private void download() {
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        if (TextUtils.isEmpty(downloadUrl)) {
            throw new NullPointerException("downloadUrl must not be null");
        }

        if (!EasyPermissions.hasPermissions(context, perms)) {
            EasyPermissions.requestPermissions(context, "updater 需要sd卡权限",
                    RC_SDCARD, perms);
            return;
        }
        if (downloadManager == null) {
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        //获取一个下载请求
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        //设置wifi，流量都可以下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager
                .Request.NETWORK_WIFI);

        //漫游网络是否可以下载
        request.setAllowedOverRoaming(allowedOverRoaming);

        //在通知栏中显示标题，默认就是显示的
        request.setTitle(TextUtils.isEmpty(title) ? apkFileName : title);

        //设置隐藏通知栏下载
        request.setNotificationVisibility(hideNotification ? DownloadManager.Request.VISIBILITY_HIDDEN
                : DownloadManager.Request.VISIBILITY_VISIBLE);

        if (!apkFileName.endsWith(".apk")) {
            apkFileName += ".apk";
        }

        //设置下载路径
        if (TextUtils.isEmpty(apkFilePath) && TextUtils.isEmpty(apkDirName)) {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFileName);
        } else if (!TextUtils.isEmpty(apkDirName)) {
            request.setDestinationInExternalPublicDir(apkDirName, apkFileName);
        } else {
            String apkAbsPath = apkFilePath + File.separator + apkFileName;
            request.setDestinationUri(Uri.fromFile(new File(apkAbsPath)));
        }

        //将下载请求加入下载队列
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等
//        LogUtils.debug("mTaskId === "+mTaskId);
//        Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(mTaskId));
//        if (cursor != null && cursor.moveToFirst()) {
//            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
//            Toast.makeText(context, "" + status, Toast.LENGTH_LONG).show();
//            LogUtils.debug("status === " + status);
//            cursor.close();
//        } else {
//            LogUtils.debug("cursor === null");
//        }
//
//        if (mTaskId != 0) {
//            downloadManager.remove(mTaskId);
//        }
        mTaskId = downloadManager.enqueue(request);

    }

    /**
     * 注册下载完成的监听
     */
    public void registerDownloadReceiver() {
        if (downloadReceiver == null) {
            downloadReceiver = new DownloadReceiver();
        }
        context.registerReceiver(downloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 解绑下载完成的监听
     */
    public void unRegisterDownloadReceiver() {
        if (downloadReceiver != null) {
            context.unregisterReceiver(downloadReceiver);
        }
    }

    private ArrayList<ProgressListener> listeners;

    /**
     * 添加下载进度回调
     */
    public void addProgressListener(ProgressListener progressListener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        if (!listeners.contains(progressListener)) {
            listeners.add(progressListener);
        }
        if (downloadObserver == null && handler != null && downloadManager != null) {
            downloadObserver = new DownloadObserver(handler, downloadManager, mTaskId);
            context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/"),
                    true, downloadObserver);
        }
    }

    /**
     * 移除下载进度回调
     */
    public void removeProgressListener(ProgressListener progressListener) {
        if (!listeners.contains(progressListener)) {
            throw new NullPointerException("this progressListener not attch Updater");
        }
        if (listeners != null && !listeners.isEmpty()) {
            listeners.remove(progressListener);
            if (listeners.isEmpty() && downloadObserver != null)
                context.getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }

    /**
     * 请求权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param receivers
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults, Object... receivers) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, receivers);
    }

    /**
     * 请求权限通过
     *
     * @param requestCode
     * @param perms
     */
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        download();
        if (listeners != null && !listeners.isEmpty()) {
            for (ProgressListener listener : listeners) {
                addProgressListener(listener);
            }
        }
    }

    /**
     * 请求权限拒绝
     *
     * @param requestCode
     * @param perms
     */
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        download();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle data = msg.getData();
            long cutBytes = data.getLong(DownloadObserver.CURBYTES);
            long totalBytes = data.getLong(DownloadObserver.TOTALBYTES);
            int progress = data.getInt(DownloadObserver.PROGRESS);
            if (listeners != null && !listeners.isEmpty()) {
                for (ProgressListener listener : listeners) {
                    listener.onProgressChange(totalBytes, cutBytes, progress);
                }
            }
            return false;
        }
    });

    public static class Builder {

        private Updater mUpdater;

        public Builder(Activity context) {
            synchronized (Updater.class) {
                if (mUpdater == null) {
                    synchronized (Updater.class) {
                        mUpdater = new Updater(context);
                    }
                }
            }
        }

        /**
         * 设置下载下来的apk文件名
         *
         * @param apkName apk文件的名字
         * @return
         */
        public Builder setApkFileName(String apkName) {
            mUpdater.apkFileName = apkName;
            return this;
        }

        /**
         * 设置apk下载的路径
         *
         * @param apkPath 自定义的全路径
         * @return
         */
        public Builder setApkPath(String apkPath) {
            mUpdater.apkFilePath = apkPath;
            return this;
        }

        /**
         * 设置下载apk的文件目录
         *
         * @param dirName sd卡的文件夹名字
         * @return
         */
        public Builder setApkDir(String dirName) {
            mUpdater.apkDirName = dirName;
            return this;
        }

        /**
         * 设置下载的链接地址
         *
         * @param downloadUrl apk的下载链接
         * @return
         */
        public Builder setDownloadUrl(String downloadUrl) {
            mUpdater.downloadUrl = downloadUrl;
            return this;
        }

        /**
         * 通知栏显示的标题
         *
         * @param title 标题
         * @return
         */
        public Builder setNotificationTitle(String title) {
            mUpdater.title = title;
            return this;
        }

        /**
         * 隐藏通知栏
         *
         * @return
         */
        public Builder hideNotification() {
            mUpdater.hideNotification = true;
            return this;
        }

        /**
         * 是否为debug模式，会输出很多log信息（手动斜眼）
         *
         * @return
         */
        public Builder debug() {
            LogUtils.isDebug = true;
            return this;
        }

        /**
         * 允许漫游网络可下载
         *
         * @return
         */
        public Builder allowedOverRoaming() {
            mUpdater.allowedOverRoaming = true;
            return this;
        }


        public Builder clearCache() {
            mUpdater.claerCache = true;
            return this;
        }

        /**
         * 开始下载
         *
         * @return
         */
        public Updater start() {
            mUpdater.download();
            return mUpdater;
        }

    }

}
