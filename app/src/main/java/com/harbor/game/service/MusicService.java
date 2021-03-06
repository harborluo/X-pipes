package com.harbor.game.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.harbor.game.util.ApplicationConfig;

import java.io.IOException;

/**
 * Created by harbor on 8/23/2016.
 */
public class MusicService extends Service{

    private MediaPlayer mediaPlayer = null;

    private boolean isReady = false;

//    @Override
//    public void onCreate() {
//        //onCreate在Service的生命周期中只会调用一次
//        super.onCreate();
//
//   //     initMediaPlayer( R.raw.main_background);
//
//    }

    private void initMediaPlayer(int resourceId, boolean bgMusicOn){

        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                //停止播放音乐
                mediaPlayer.stop();

            }
            //释放媒体播放器资源
            mediaPlayer.release();
            //Toast.makeText(this, "停止播放背景音乐", Toast.LENGTH_LONG).show();
            mediaPlayer=null;
        }

        Log.i("Music service", "initMediaPlayer: backgroundMusicOn = " + ApplicationConfig.getInstance().isBackgroundMusicOn());

        if(bgMusicOn==false){
            return;
        }

        //初始化媒体播放器
        mediaPlayer = MediaPlayer.create(this, resourceId);

        if(mediaPlayer == null){
            return;
        }

        mediaPlayer.stop();
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                mp.release();
                stopSelf();
                return false;
            }
        });

        try{
            mediaPlayer.prepare();
            isReady = true;
        } catch (IOException e) {
            e.printStackTrace();
            isReady = false;
        }

        if(isReady){
            //将背景音乐设置为循环播放
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(50,50);

        }
    }

    /**
     * 每次调用Context的startService都会触发onStartCommand回调方法
     * 所以onStartCommand在Service的生命周期中可能会被调用多次
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("MusicService", "onStartCommand: music resource id = " + intent.getIntExtra("music",-1)
                +", backgroundMusicOn = "+ApplicationConfig.getInstance().isBackgroundMusicOn());

        boolean bgMusicOn = intent.getBooleanExtra("background_music_on",true);

        initMediaPlayer(intent.getIntExtra("music",-1),bgMusicOn);

        if(isReady && !mediaPlayer.isPlaying()&&bgMusicOn){
            //播放背景音乐
            mediaPlayer.start();
           // Toast.makeText(this, "开始播放背景音乐", Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //该Service中不支持bindService方法，所以此处直接返回null
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i("MusicService", "onDestroy: music service shut down.");

        //当调用Context的stopService或Service内部执行stopSelf方法时就会触发onDestroy回调方法
        super.onDestroy();
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                //停止播放音乐
                mediaPlayer.stop();
            }
            //释放媒体播放器资源
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
            //Toast.makeText(this, "停止播放背景音乐", Toast.LENGTH_LONG).show();
        }
    }

}
