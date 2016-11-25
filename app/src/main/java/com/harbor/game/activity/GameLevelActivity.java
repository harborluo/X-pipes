package com.harbor.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.harbor.game.GameLevelConfig;
import com.harbor.game.R;
import com.harbor.game.util.DBHelper;
import com.harbor.game.widget.GameLevelAdapter;

import java.util.List;

public class GameLevelActivity extends AbstractActivity implements View.OnClickListener {

    DBHelper dbHelper = null;

    GridView gridView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_level);
        dbHelper = new DBHelper(this);

        gridView = (GridView) findViewById(R.id.levelGridView);

        DisplayMetrics displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int width = screenWidth/5;
        int height = screenHeight/5;

        List<GameLevelConfig> dataList = dbHelper.getAllGameLevel();//= new ArrayList<>();

//        for(int i=0;i<25;i++){
//            dataList.add(new GameLevelConfig(i+1,i>5,0));
//        }

        gridView.setAdapter(new GameLevelAdapter(this,this,width, height,dataList));
        gridView.setNumColumns(5);

    }


    @Override
    public void onClick(View view) {

        GameLevelConfig level = (GameLevelConfig) view.getTag();
        if(level.isLocked()){
            Toast.makeText(this,"Level "+ level.getLevel()+" is locked.",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(GameLevelActivity.this, GameActivity.class);
        intent.putExtra("gameLevel", level.getLevel());
        startActivity(intent);

    }
}
