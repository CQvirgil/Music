package com.virgil.study.music.Music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

public class MyMusicService extends Service {
    private static MyMusicService myMusicService;
    private MediaPlayer mediaPlayer;
    private String url;
    private boolean isOnCompletion=false;
    public MyMusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

    public void setMediaPlayer() throws IOException {
        mediaPlayer.setDataSource(this, Uri.parse("file://"+url));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isOnCompletion = true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void  startMusic() throws IOException {
        isOnCompletion = false;
        setMediaPlayer();
        mediaPlayer.prepare();
        System.out.println("startMusic");
    }

    public void pauseMusic(){
        mediaPlayer.pause();
    }

    public void nextMusic() throws IOException {
        System.out.println("next");
        mediaPlayer.stop();
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        setMediaPlayer();
        mediaPlayer.prepare();
        isOnCompletion = false;
    }

    public void backMusic() throws IOException {
        mediaPlayer.stop();
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        setMediaPlayer();
        mediaPlayer.prepare();
        isOnCompletion = false;
    }

    private class MusicBinder extends Binder implements LinkMusicService{
        @Override
        public void StartMusic() {
            try {
                startMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void PauseMusic() {
            pauseMusic();
        }

        @Override
        public void NextMusic() {
            try {
                nextMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void BackMusic() {
            try {
                backMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getDuration() {//音乐长度
            return mediaPlayer.getDuration();
        }

        @Override
        public int getCurrenPosition() {//音乐当前进度
            return mediaPlayer.getCurrentPosition();
        }

        @Override
        public void Seekto(int mesc) {//设置音乐进度
         mediaPlayer.seekTo(mesc);
        }

        @Override
        public void setUrl(String musicurl) {
            url = musicurl;
        }

        @Override
        public void Continue() {
            mediaPlayer.start();
        }

        @Override
        public boolean getisOnCompletion() {
            return isOnCompletion;
        }
    }
}