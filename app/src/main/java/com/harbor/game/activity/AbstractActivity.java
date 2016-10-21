package com.harbor.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.harbor.game.service.MusicService;
import com.harbor.game.util.ApplicationConfig;

/**
 * Created by harbor on 10/20/2016.
 */
public class AbstractActivity extends Activity {
    
    private String TAG = "AbstractActivity";

    /**
     *
     */
    public void playMusic(int musicResourceId){
        Log.d(TAG, "playMusic: backgroundMusicOn = " + ApplicationConfig.getInstance().isBackgroundMusicOn());
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("music", musicResourceId);
        intent.putExtra("background_music_on", ApplicationConfig.getInstance().isBackgroundMusicOn());
        startService(intent);
    }

    public void stopMusic(){
        Log.d(TAG, "stopMusic: ");
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }
}
