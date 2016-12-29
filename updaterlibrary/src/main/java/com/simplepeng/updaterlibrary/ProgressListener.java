package com.simplepeng.updaterlibrary;

/**
 * Created by simple on 16/12/19.
 */

public interface ProgressListener {
    void onProgressChange(long totalBytes, long curBytes, int progress);
}
