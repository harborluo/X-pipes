package com.harbor.game.widget;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
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

    private String TAG = "GameDataAdapter";

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        ViewHolder viewHolder=null;
        GameData gameData = games.get(position);

        if(convertView==null){

            viewHolder = new ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.single_game_data, null, true);

            viewHolder.levelTextView = (TextView) convertView.findViewById(R.id.txtLev);
            viewHolder.reqTextView = (TextView) convertView.findViewById(R.id.txtReq);
            viewHolder.scoreTextView = (TextView) convertView.findViewById(R.id.txtScore);

           // viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.secondsTextView = (TextView) convertView.findViewById(R.id.txtSeconds);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.gameProgressBar);

            viewHolder.gridView = (GridView) convertView.findViewById(R.id.preview_container);
            viewHolder.gridView.setTag(position);

            convertView.setTag(viewHolder);

//            viewHolder.gridView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Toast.makeText(this,"",2).show();
//                    Toast.makeText(parent.getContext(), "next pipe is block = ", Toast.LENGTH_SHORT).show();
//                }
//            });
//            viewHolder.gridView.setOnItemClickListener(null);
//            viewHolder.gridView.setOnClickListener(null);

//            viewHolder.gridView.setOnTouchInvalidPositionListener(new OnTouchInvalidPositionListener() {
//
//                @Override
//                public boolean onTouchInvalidPosition(int motionEvent) {
//                /*当返回false的时候代表交由父级控件处理，当return true的时候表示你已经处理了该事件并不
//
//让该事件再往上传递。为了出发listview的item点击就得返回false了*/
//                    return false;
//                }
//            });

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        int pipeWidth = screenWidth / 4 / gameData.getNumOfColumns();

        Log.i(TAG, "getView: pipeWidth = " + pipeWidth);

        ViewGroup.LayoutParams params = viewHolder.gridView.getLayoutParams();
        // 设置高度
        params.height = pipeWidth * gameData.getNumOfRows();
        params.width = pipeWidth  * gameData.getNumOfColumns();

        // 设置参数
        viewHolder.gridView.setLayoutParams(params);

        viewHolder.gridView.setNumColumns(gameData.getNumOfColumns());
        viewHolder.gridView.setAdapter(new ImageAdapter(context, null, pipeWidth, gameData.getData()));

        viewHolder.levelTextView.setText("" + gameData.getLevel());
        viewHolder.reqTextView.setText(""+gameData.getMissionCount());
        viewHolder.scoreTextView.setText(""+gameData.getTotalScore());
       // viewHolder.dateTextView.setText(gameData.getDateCreated());
        viewHolder.secondsTextView.setText(gameData.getSecondRemain()+"");
        viewHolder.progressBar.setProgress(gameData.getProgress());

        return convertView;
    }

    class ViewHolder{
        TextView levelTextView;
        TextView reqTextView;
        TextView scoreTextView ;
        TextView secondsTextView ;
      //  TextView dateTextView ;
        GridView gridView  ;
        ProgressBar progressBar;
    }

}


