package com.harbor.game.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.harbor.game.GameLevelConfig;

import java.util.List;

/**
 * Created by harbor on 11/25/2016.
 */
public class GameLevelAdapter extends BaseAdapter {


    private final Context mContext;

    private final View.OnClickListener listener;

    private final int width, height;

    private final List<GameLevelConfig> cache;

    public GameLevelAdapter(Context c, View.OnClickListener listener, int width, int height, List<GameLevelConfig> list) {
        
        this.mContext = c;

        this.listener = listener;

        this.width = width;

        this.height = height;

        this.cache = list;
    }
    
    public int getCount() {
        return cache.size();
    }

    @Override
    public Object getItem(int i) {
        return cache.get(i);
    }

    @Override
    public long getItemId(int i) {
        return cache.get(i).getLevel();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView!=null) {
            return  convertView;
        }

        boolean locked = cache.get(position).isLocked();

        TextView textView = new TextView(mContext);
        textView.setText(cache.get(position).getLevel()+"");

        textView.setLayoutParams(new GridView.LayoutParams(this.width, this.height));
        textView.setId(position);
        textView.setTextSize(20);

        textView.setTextColor(locked?Color.DKGRAY:Color.GREEN);

//        textView.setBackgroundColor(Color.alpha(5));

        textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

        if(listener!=null){
            textView.setOnClickListener(listener);
        }

        textView.setTag(cache.get(position));

        return textView;
    }
}
