package com.virgil.study.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.virgil.study.music.Music.LinkMusicService;
import com.virgil.study.music.Music.MyMusicService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    Button button_pause;
    List<MusicInfo> musiclist;
    MusicInfo musicInfo;
    int listposition = 0;
    Intent intent;
    private LinkMusicService linkmusicservice;
    private Connection connection;
    private Boolean isplay = true;
    private SeekBar seekbar;
    private Handler hander;
    private int SEEKBAR_START = 1;
    private int NEXT = 2, BACK = 3;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private List<String> musicstringlist;
    private Thread isOnCompletionthread, seekbarThread;
    private LinearLayoutManager layoutManager;
    private int ClickBack = 0;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            requestReadExternalPermission();
        }
        System.out.println("API: " + currentapiVersion);
        initUI();
        initThread();
    }

    private void initView(){

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    private void initThread() {
        hander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == SEEKBAR_START) {
                    setSeekbar();
                } else if (msg.what == NEXT) {
                    Move_Next();
                } else if (msg.what == BACK) {
                    Move_Back();
                }
            }
        };

        //seekbar更新线程
        seekbarThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        hander.sendEmptyMessage(SEEKBAR_START);
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //监听是否播放完毕
        isOnCompletionthread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (linkmusicservice.getisOnCompletion() && listposition < musiclist.size() - 1) {
                            hander.sendEmptyMessage(NEXT);
                        }
                        Thread.sleep(2500);
                        //System.out.println(linkmusicservice.getisOnCompletion());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initUI() {
        try {
            tv = findViewById(R.id.tv);
            seekbar = findViewById(R.id.sb);
            intent = new Intent(MainActivity.this, MyMusicService.class);
            connection = new Connection();
            button_pause = findViewById(R.id.button_pause);

            musiclist = Util.get(this).getMusic();//获取音乐列表
            tv.setText(musiclist.get(listposition).getTitle() + "-" + musiclist.get(listposition).getSinger());
            bindService(intent, connection, BIND_AUTO_CREATE);
            startService(intent);
            //适配列表
            recyclerView = findViewById(R.id.rv);
            musicstringlist = new ArrayList<>();
            for (int i = 0; i < musiclist.size(); i++) {
                musicstringlist.add(musiclist.get(i).getTitle() + " - " + musiclist.get(i).getSinger());
            }
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerAdapter(getApplicationContext(), 2, musicstringlist);
            recyclerView.setAdapter(adapter);
            //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {//item点击事件
                    //Toast.makeText(MainActivity.this,"您点击了"+position+"行", Toast.LENGTH_SHORT).show();
                    listposition = position;
                    if(musiclist.get(listposition).getUrl()!= null){
                        linkmusicservice.setUrl(musiclist.get(listposition).getUrl());
                        linkmusicservice.NextMusic();
                        tv.setText(musiclist.get(listposition).getTitle() + "-" + musiclist.get(listposition).getSinger());
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSeekbar() {
        seekbar.setProgress(linkmusicservice.getCurrenPosition());

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    linkmusicservice.Seekto(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //linkmusicservice.PauseMusic();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //linkmusicservice.Continue();
            }
        });
    }

    //监听返回按钮
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 4 && ClickBack < 1) {
            ClickBack++;
            Toast.makeText(MainActivity.this, "再次点击退出", Toast.LENGTH_SHORT).show();
            return false;
        } else if (ClickBack == 0) {
            this.finish();
            return false;
        } else return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        stopService(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {//API23调用此方法获取权限
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
            default:
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    //按钮点击事件
    public void button_click(View view) {
        switch (view.getId()) {
            case R.id.button_back:
                if (listposition > 0) {
                    Move_Back();
                }
                break;

            case R.id.button_pause:
                if (isplay) {
                    button_pause.setText("播放");
                    linkmusicservice.PauseMusic();
                    isplay = false;
                } else {
                    button_pause.setText("暂停");
                    linkmusicservice.Continue();
                    isplay = true;
                }

                break;

            case R.id.button_next:
                if (listposition < musiclist.size() - 1) {
                    Move_Next();
                }
                break;
        }
    }

    private void Move_Back() {
        listposition--;
        System.out.println(listposition);
        tv.setText(musiclist.get(listposition).getTitle() + "-" + musiclist.get(listposition).getSinger());
        linkmusicservice.setUrl(musiclist.get(listposition).getUrl());
        linkmusicservice.BackMusic();
        seekbar.setMax(linkmusicservice.getDuration());
    }

    private void Move_Next() {
        listposition++;
        System.out.println(listposition);
        tv.setText(listposition + "");
        System.out.println(musiclist.size() - 1);
        tv.setText(musiclist.get(listposition).getTitle() + "-" + musiclist.get(listposition).getSinger());
        linkmusicservice.setUrl(musiclist.get(listposition).getUrl());
        linkmusicservice.NextMusic();
        seekbar.setMax(linkmusicservice.getDuration());
    }

    //连接服务
    private class Connection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            linkmusicservice = (LinkMusicService) iBinder;
            System.out.println("Bind");
            linkmusicservice.setUrl(musiclist.get(listposition).getUrl());
            linkmusicservice.StartMusic();
            seekbar.setMax(linkmusicservice.getDuration());
            seekbar.setProgress(linkmusicservice.getCurrenPosition());
            //hander.sendEmptyMessage(SEEKBAR_START);
            seekbarThread.start();
            isOnCompletionthread.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}