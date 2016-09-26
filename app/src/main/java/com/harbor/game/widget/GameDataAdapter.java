package com.harbor.game.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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



        levelTextView.setText(games.get(position).getLevel()+"");
        scoreTextView.setText(games.get(position).getTotalScore()+"");
        dateTextView.setText(games.get(position).getDateCreated());

        return rowView;
    }

}


