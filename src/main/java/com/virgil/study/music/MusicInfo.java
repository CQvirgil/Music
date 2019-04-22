package com.virgil.study.music;

public class MusicInfo {
    String title;
    String url;
    int duration;
    String singer;
    String album;
    long size;

    public MusicInfo(String title, String url, int duration, String singer, String album, long size) {
        this.title = title;
        this.url = url;
        this.duration = duration;
        this.singer = singer;
        this.album = album;
        this.size = size;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSinger() {
        return singer;
    }

    public String getAlbum() {
        return album;
    }

    public long getSize() {
        return size;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }
}
