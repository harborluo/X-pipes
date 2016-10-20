package com.harbor.game.activity;

import android.app.Activity;
import android.content.Intent;

import com.harbor.game.service.MusicService;

/**
 * Created by harbor on 10/20/2016.
 */
public class AbstractActivity extends Activity {

    /**
     *
     */
    public void playMusic(int musicResourceId){
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("music", musicResourceId);
        startService(intent);
    }

    public void stopMusic(){
            Intent intent = new Intent(this, MusicService.class);
            stopService(intent);
            return;

    }
}
