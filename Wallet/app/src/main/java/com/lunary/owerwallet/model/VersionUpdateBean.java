package com.lunary.owerwallet.model;

/**
 * Created by Administrator on 2018/8/11.
 */
public class VersionUpdateBean {
    private String lastestVersion;
    private String lastestVersionUrl;
    private boolean forceUpdate;//是否要强制下载
    private boolean suggestUpdate;//是否有最新版本
    private String lastestVersionName;
    private String lastestVersionDesc;

    public String getLastestVersionDesc() {
        return lastestVersionDesc;
    }

    public void setLastestVersionDesc(String lastestVersionDesc) {
        this.lastestVersionDesc = lastestVersionDesc;
    }

    public String getLastestVersion() {
        return lastestVersion;
    }

    public void setLastestVersion(String lastestVersion) {
        this.lastestVersion = lastestVersion;
    }

    public String getLastestVersionUrl() {
        return lastestVersionUrl;
    }

    public void setLastestVersionUrl(String lastestVersionUrl) {
        this.lastestVersionUrl = lastestVersionUrl;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isSuggestUpdate() {
        return suggestUpdate;
    }

    public void setSuggestUpdate(boolean suggestUpdate) {
        this.suggestUpdate = suggestUpdate;
    }

    public String getLastestVersionName() {
        return lastestVersionName;
    }

    public void setLastestVersionName(String lastestVersionName) {
        this.lastestVersionName = lastestVersionName;
    }
}
