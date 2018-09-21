package com.simplepeng.updater;


public interface ProgressListener {

    void onProgressChange(long totalBytes, long curBytes, int progress);
}
