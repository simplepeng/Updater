package com.simplepeng.updaterlibrary;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by simple on 16/12/20.
 */

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        long downId = bundle.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Uri fileUri = downloadManager.getUriForDownloadedFile(downId);
        if (intent.getAction() == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            installApk(context, fileUri);
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            installApk(context, fileUri);
        }
    }

    //安装apk
    private void installApk(Context context, Uri fileUri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


}
