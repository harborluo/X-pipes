package com.harbor.game.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.harbor.game.service.MusicService;

/**
 * Created by harbor on 10/20/2016.
 */
public class AbstractActivity extends Activity {

    private SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("pipe", Context.MODE_PRIVATE);
    }

    public  boolean isBackgroundMusicOn() {
        return pref.getBoolean("backgroundMusicOn",true);
    }

    public void setBackgroundMusicOn(boolean backgroundMusicOn) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("backgroundMusicOn",backgroundMusicOn);
        editor.commit();
    }

    public  boolean isGameSoundOn() {
        return pref.getBoolean("gameSoundOn",true);
    }

    public void setGameSoundOn(boolean gameSoundOn) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("gameSoundOn",gameSoundOn);
        editor.commit();
    }

    public  boolean isGameAnimationOn() {
        return pref.getBoolean("gameAnimationOn",true);
    }

    public  void setGameAnimationOn(boolean gameAnimationOn) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("gameAnimationOn",gameAnimationOn);
        editor.commit();
    }

    public  String getLang() {
        return pref.getString("lang","cn");
    }

    public void setLang(String lang) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lang",lang);
        editor.commit();
    }
    
    private String TAG = "AbstractActivity";

    /**
     *
     */
    public void playMusic(int musicResourceId){
        Log.d(TAG, "playMusic: backgroundMusicOn = " + isBackgroundMusicOn());
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("music", musicResourceId);
        intent.putExtra("background_music_on", isBackgroundMusicOn());
        startService(intent);
    }

    public void stopMusic(){
        Log.d(TAG, "stopMusic: ");
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }

    public void reset() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("backgroundMusicOn",true);
        editor.putBoolean("gameSoundOn",true);
        editor.putString("lang","en");
        editor.putBoolean("gameAnimationOn",true);
        editor.commit();
    }
}
