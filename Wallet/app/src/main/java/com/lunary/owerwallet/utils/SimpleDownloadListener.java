package com.lunary.owerwallet.utils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

/**
 * Created by wxliao on 2016/2/22.
 */
public class SimpleDownloadListener extends FileDownloadListener {
    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
    }

    @Override
    protected void completed(BaseDownloadTask task) {
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
    }

    @Override
    protected void warn(BaseDownloadTask task) {

    }
}
