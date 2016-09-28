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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder=null;
        GameData gameData = games.get(position);

        if(convertView==null){

            viewHolder = new ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.single_game_data, null, true);

            viewHolder.levelTextView = (TextView) convertView.findViewById(R.id.txtLev);
            viewHolder.scoreTextView = (TextView) convertView.findViewById(R.id.txtScore);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.gridView = (GridView) convertView.findViewById(R.id.preview_container);



            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        int pipeWidth = screenWidth/4/gameData.getNumOfColumns();

        ViewGroup.LayoutParams params = viewHolder.gridView.getLayoutParams();
        // 设置高度
        params.height = pipeWidth * gameData.getNumOfRows();
        params.width = pipeWidth  * gameData.getNumOfColumns();

        // 设置参数
        viewHolder.gridView.setLayoutParams(params);

        viewHolder.gridView.setNumColumns(gameData.getNumOfColumns());
        viewHolder.gridView.setAdapter(new ImageAdapter(context, null, pipeWidth, gameData.getData()));

        viewHolder.levelTextView.setText("Level : " + gameData.getLevel());
        viewHolder.scoreTextView.setText("Score : "+gameData.getTotalScore()+"");
        viewHolder.dateTextView.setText(gameData.getDateCreated());

        return convertView;
    }

    class ViewHolder{
        TextView levelTextView;
        TextView scoreTextView ;
        TextView dateTextView ;
        GridView gridView  ;
    }

}


