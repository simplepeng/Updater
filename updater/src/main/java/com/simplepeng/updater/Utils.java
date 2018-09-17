package com.simplepeng.updater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

public class Utils {

    public static final String getFileNameForUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url is null");
        }

        return url.substring(url.lastIndexOf("/") + 1);
    }

    public static void installApk(Context context, Uri uri) {
        File file = new File(uri.getPath());
        if (!file.exists()) {
            LogUtils.debug("apk file not exists");
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            String packageName = context.getPackageName();
            LogUtils.debug("packageName==" + packageName);
            Uri providerUri = FileProvider
                    .getUriForFile(context, packageName + ".fileProvider", file);
//            Uri providerUri = FileProvider
//                    .getUriForFile(context, "com.simplepeng.updaterlibrary.fileprovider", file);
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
