package com.simplepeng.updaterlibrary;

/**
 * Created by simple on 16/12/19.
 */

public interface ProgressListener {

    /**
     *
     * @param totalBytes 总共要下载的字节数
     * @param curBytes 当前下载的字节数
     * @param progress 当前的进度
     */
    void onProgressChange(long totalBytes, long curBytes, int progress);
}
