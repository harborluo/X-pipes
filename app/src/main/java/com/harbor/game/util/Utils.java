package com.harbor.game.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.TypedValue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by harbor on 8/20/2016.
 */
public class Utils {

    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    /**
     * 获取默认的文件路径
     *
     * @return
     */
    public static String getDefaultFilePath() {
        String filepath = null;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)==false){
            return filepath;
        }

     //   return  Environment.getDataDirectory().getAbsolutePath();

        return Environment.getExternalStorageDirectory().getPath();

    }

    public static void saveObject(Object obj, String fileName){
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Object readObject(String fileName){
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();

            return obj;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
