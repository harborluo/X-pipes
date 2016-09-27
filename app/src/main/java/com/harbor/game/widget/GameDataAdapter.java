package com.harbor.game.widget;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.harbor.game.GameData;
import com.harbor.game.R;

import java.util.List;

/**
 * Created by harbor on 9/26/2016.
 */
public class GameDataAdapter extends ArrayAdapter<GameData> {

    private final Activity context;

    List<GameData> games = null;

    public GameDataAdapter(Activity context,List<GameData> games) {
        super(context, R.layout.single_game_data, games);
        this.context = context;
        this.games = games;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.single_game_data, null, true);

        TextView levelTextView = (TextView) rowView.findViewById(R.id.txtLev);
        TextView scoreTextView = (TextView) rowView.findViewById(R.id.txtScore);
        TextView dateTextView = (TextView) rowView.findViewById(R.id.txtDate);

        GridView gridView = (GridView) rowView.findViewById(R.id.preview_container);

        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
    //    int screenHeight = displayMetrics.heightPixels;

        GameData gameData = games.get(position);

        int pipeWidth = screenWidth/4/gameData.getNumOfColumns();

//        pipeWidth=10;

       // gridView.setLayoutParams(new GridView.LayoutParams(pipeWidth+gameData.getNumOfColumns(), pipeWidth+gameData.getNumOfRows()));

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        // 设置高度
        params.height = pipeWidth * gameData.getNumOfRows();
        params.width = pipeWidth  * gameData.getNumOfColumns();

        // 设置参数
        gridView.setLayoutParams(params);

        gridView.setNumColumns(gameData.getNumOfColumns());
        gridView.setAdapter(new ImageAdapter(context, null, pipeWidth, gameData.getData()));

        levelTextView.setText("Level : " + gameData.getLevel());
        scoreTextView.setText("Score : "+gameData.getTotalScore()+"");
        dateTextView.setText(gameData.getDateCreated());

        return rowView;
    }

}


