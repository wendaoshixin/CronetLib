package com.leimans.lib.cronetlib;

/**
 * @Author ：FengLi
 * @Date : 2021-03-12
 * @Description :
 */
public interface UploadProgressListener {
    void onProgress(long totalLength, long mCurrentLength);
}
