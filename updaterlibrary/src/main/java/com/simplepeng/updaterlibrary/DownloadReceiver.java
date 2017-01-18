package com.simplepeng.updaterlibrary;

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
        if (c != null) {
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String downloadFileUrl = c
                            .getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    installApk(context, Uri.parse(downloadFileUrl));
                }
            }
            c.close();
        }
    }

    private void installApk(Context context, Uri uri) {
        File file = new File(uri.getPath());
        if (!file.exists()) {
            LogUtils.debug("apk file not exists");
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Uri providerUri = FileProvider
//                    .getUriForFile(context, "com.simplepeng.updater.fileprovider", file);
            Uri providerUri = FileProvider
                    .getUriForFile(context, "com.simplepeng.updaterlibrary.fileprovider", file);
            LogUtils.debug("providerUri==" + providerUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(providerUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent);
    }
}
