package com.harbor.game.widget;

/**
 * Created by harbor on 8/20/2016.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.harbor.game.R;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private View.OnClickListener listener;

    private int imageWidth = 0;
    //private int count;

    // all Images in array
    public int[] itemCache = null;

    // Constructor
    public ImageAdapter(Context c, View.OnClickListener listener, int imageWidth, int[] data) {
        this.mContext = c;
        this.listener = listener;
        this.imageWidth = imageWidth;
        this.itemCache = data;

    }

    @Override
    public int getCount() {
        return this.itemCache.length;
    }

    @Override
    public Object getItem(int position) {

        //return mThumbIds[position];
        return itemCache[position];
    }

    @Override
    public long getItemId(int position) {
        return itemCache[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView!=null) {
            return  convertView;
        }

        ImageView imageView = new ImageView(mContext);

        imageView.setBackgroundResource(R.mipmap.blank);

         if(itemCache[position]!=R.mipmap.blank){
            imageView.setImageResource(itemCache[position]);
         }

         imageView.setScaleType(ImageView.ScaleType.FIT_XY);
         imageView.setLayoutParams(new GridView.LayoutParams(this.imageWidth, this.imageWidth));
         imageView.setId(position);

         if(listener!=null){
            imageView.setOnClickListener(listener);
         }

         imageView.setTag(itemCache[position]);

         return imageView;
    }
}
