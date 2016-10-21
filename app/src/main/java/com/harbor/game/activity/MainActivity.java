
package com.harbor.game.activity;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.harbor.game.R;
import com.harbor.game.util.ApplicationConfig;

import java.util.Locale;

public class MainActivity extends AbstractActivity implements View.OnClickListener{

    Button btn_quit_game,btn_new_game, btn_help, btn_load, btn_setting = null;

    private static String TAG = "MainActivity";

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // playMusic(R.raw.main_background);
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: ");

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(ApplicationConfig.getInstance().getLang()));
        res.updateConfiguration(conf, dm);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_quit_game = (Button) findViewById(R.id.btn_quit_game);
        btn_quit_game.setOnClickListener(this);

        btn_new_game = (Button) findViewById(R.id.btn_new_game);
        btn_new_game.setOnClickListener(this);

        btn_help = (Button) findViewById(R.id.btn_help);
        btn_help.setOnClickListener(this);

        btn_load = (Button) findViewById(R.id.btn_load_game);
        btn_load.setOnClickListener(this);

        btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(this);

        playMusic(R.raw.main_background);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        playMusic(R.raw.main_background);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_quit_game : {
                MainActivity.this.finish();
            }
            break;

            case R.id.btn_new_game :{
                Intent intent = new Intent();//new Intent(MainActivity.this,GameActivity.class);
                intent.setAction("game");
                startActivity(intent);
            }
            break;

            case R.id.btn_help :{
                Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_load_game :{
                Intent intent = new Intent(MainActivity.this,LoadGameActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_setting:{
//                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
//                startActivity(intent);
                showSettingDialog();
            }
            break;

        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, getResources().getString(R.string.game_text_label_back_prompt), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void showSettingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);

        final View view = inflater.inflate(R.layout.activity_setting,null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setCancelable(false);

        builder.setView(view);

        //dialog.setTitle("Game setting");
        TextView title = new TextView(this);
// You Can Customise your Title here
        title.setText(R.string.game_text_label_setting);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder.setCustomTitle(title);

        final AlertDialog dialog = builder.create();
//        dialog.setIcon(R.mipmap.ic_launcher);

        final Button saveButton = (Button) view.findViewById(R.id.btn_save_setting);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox animationBox = (CheckBox) view.findViewById(R.id.checkBox_animation);
                CheckBox soundBox = (CheckBox) view.findViewById(R.id.checkBox_sound);
                CheckBox backgroundMusicBox = (CheckBox) view.findViewById(R.id.checkBox_bg_music);
                RadioButton enButton = (RadioButton)  view.findViewById(R.id.lang_english);

              //  RadioButton cnButton = (RadioButton)  view.findViewById(R.id.lang_chinese);

                boolean backgroundMusicChanged = backgroundMusicBox.isChecked()!=ApplicationConfig.getInstance().isBackgroundMusicOn();

                ApplicationConfig.getInstance().setGameAnimationOn(animationBox.isChecked());
                ApplicationConfig.getInstance().setGameSoundOn(soundBox.isChecked());
                ApplicationConfig.getInstance().setBackgroundMusicOn(backgroundMusicBox.isChecked());
                ApplicationConfig.getInstance().setLang(enButton.isChecked()?"en":"cn");

                if(backgroundMusicChanged){

                  if(backgroundMusicBox.isChecked()==false){
                     MainActivity.this.stopMusic();
                  }else{
                      MainActivity.this.playMusic(R.raw.main_background);
                  }
                }

                ApplicationConfig.getInstance().save();

                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale(ApplicationConfig.getInstance().getLang()));
                res.updateConfiguration(conf, dm);

                MainActivity.this.onConfigurationChanged(conf);

                dialog.dismiss();

            }
        });

        final Button resetButton = (Button) view.findViewById(R.id.btn_reset_setting);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConfig.getInstance().reset();
                MainActivity.this.playMusic(R.raw.main_background);

                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale(ApplicationConfig.getInstance().getLang()));
                res.updateConfiguration(conf, dm);
                MainActivity.this.onConfigurationChanged(conf);

                dialog.dismiss();
            }
        });

        //dialog initialization
        CheckBox animationBox = (CheckBox) view.findViewById(R.id.checkBox_animation);
        CheckBox soundBox = (CheckBox) view.findViewById(R.id.checkBox_sound);
        CheckBox backgroundMusicBox = (CheckBox) view.findViewById(R.id.checkBox_bg_music);
        RadioButton enButton = (RadioButton)  view.findViewById(R.id.lang_english);
        RadioButton cnButton = (RadioButton)  view.findViewById(R.id.lang_chinese);

        animationBox.setChecked(ApplicationConfig.getInstance().isGameAnimationOn());
        soundBox.setChecked(ApplicationConfig.getInstance().isGameSoundOn());
        backgroundMusicBox.setChecked(ApplicationConfig.getInstance().isBackgroundMusicOn());

        if("en".equals(ApplicationConfig.getInstance().getLang())){
            enButton.setChecked(true);
        }else{
            cnButton.setChecked(true);
        }

        dialog .getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        btn_setting.setText(R.string.game_text_button_setting);
        btn_help.setText(R.string.game_text_button_help);
        btn_new_game.setText(R.string.game_text_button_start);
        btn_load.setText(R.string.game_text_button_load);
        btn_quit_game.setText(R.string.game_text_button_quit);
//        this.recreate();
        super.onConfigurationChanged(newConfig);
    }

}
