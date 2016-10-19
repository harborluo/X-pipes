
package com.harbor.game.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.harbor.game.R;
import com.harbor.game.service.MusicService;

public class MainActivity extends Activity implements View.OnClickListener{

    Button btn_quit_game,btn_new_game, btn_help, btn_load, btn_setting = null;

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("music",R.raw.main_background);
        stopService(intent);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("music",R.raw.main_background);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        Intent intent = new Intent(getApplicationContext(),MusicService.class);
        intent.putExtra("music",R.raw.main_background);
        startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(),MusicService.class);
        intent.putExtra("music",R.raw.main_background);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_quit_game : {
                MainActivity.this.finish();
            }
            break;

            case R.id.btn_new_game :{
                Intent intent = new Intent();//new Intent(MainActivity.this,GameActiviy.class);
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
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

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
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }

    private void showSettingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.activity_setting,null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setCancelable(false);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        final Button saveButton = (Button) view.findViewById(R.id.btn_save_setting);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        final Button resetButton = (Button) view.findViewById(R.id.btn_reset_setting);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog .getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

    }

}
