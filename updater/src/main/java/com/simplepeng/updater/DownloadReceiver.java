package com.simplepeng.updater;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by simple on 16/12/20.
 * <p>
 * 下载监听
 */

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        long downId = bundle.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        //下载完成或点击通知栏
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE) ||
                intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            queryFileUri(context, downId);
        }
    }

    private void queryFileUri(Context context, long downloadApkId) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadApkId);
        Cursor c = dManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PENDING:
                    LogUtils.debug("STATUS_PENDING");
                    break;
                case DownloadManager.STATUS_PAUSED:
                    LogUtils.debug("STATUS_PAUSED");
                    break;
                case DownloadManager.STATUS_RUNNING:
                    LogUtils.debug("STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    LogUtils.debug("STATUS_SUCCESSFUL");
                    String downloadFileUrl = c
                            .getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Utils.installApk(context, Uri.parse(downloadFileUrl));
//                    context.unregisterReceiver();
                    break;
                case DownloadManager.STATUS_FAILED:
                    LogUtils.debug("STATUS_FAILED");
                    Updater.showToast(context, "下载失败，开始重新下载...");
                    context.sendBroadcast(new Intent(Updater.DownloadFailedReceiver.tag));
                    break;
            }
            c.close();
        }
    }


}
