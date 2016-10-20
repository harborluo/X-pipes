package com.harbor.game.util;

import android.util.Log;

import java.io.File;
import java.io.Serializable;

/**
 * Created by harbor on 10/20/2016.
 */
public class ApplicationConfig implements Serializable {

    private static final ApplicationConfig instance = new ApplicationConfig();

    public ApplicationConfig(){

    }

    private  boolean backgroundMusicOn = true, gameSoundOn = true, gameAnimationOn=true;

    private  String lang = "en";//cn

    public  boolean isBackgroundMusicOn() {
        return backgroundMusicOn;
    }

    public void setBackgroundMusicOn(boolean backgroundMusicOn) {
        this.backgroundMusicOn = backgroundMusicOn;
    }

    public  boolean isGameSoundOn() {
        return gameSoundOn;
    }

    public void setGameSoundOn(boolean gameSoundOn) {
        this.gameSoundOn = gameSoundOn;
    }

    public  boolean isGameAnimationOn() {
        return gameAnimationOn;
    }

    public  void setGameAnimationOn(boolean gameAnimationOn) {
        this.gameAnimationOn = gameAnimationOn;
    }

    public  String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    private static String fileName = Utils.getDefaultFilePath() + File.separator + "x_pipe.config";

    static{

        Object obj = Utils.readObject(fileName);

        if(obj!=null){
           ApplicationConfig config = (ApplicationConfig) obj;
            instance.backgroundMusicOn = config.isBackgroundMusicOn();
            instance.gameAnimationOn = config.isGameSoundOn();
            instance.gameAnimationOn = config.isGameAnimationOn();
            instance.lang = config.getLang();
        }else{
            instance.save();
        }

    }

    public void save(){

        Log.i("ApplicationConfig", "save: backgroundMusicOn = "+backgroundMusicOn);
        Log.i("ApplicationConfig", "save: gameAnimationOn = "+gameAnimationOn);
        Log.i("ApplicationConfig", "save: gameSoundOn = "+gameSoundOn);
        Log.i("ApplicationConfig", "save: lang = "+lang);

        Utils.saveObject(instance,fileName);
    }

    public void reset(){
        instance.backgroundMusicOn = true;
        instance.gameAnimationOn = true;
        instance.gameSoundOn = true;
        instance.lang = "en";
        Utils.saveObject(instance,fileName);
    }

    public static ApplicationConfig getInstance() {
        return instance;
    }

}
