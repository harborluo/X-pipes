package com.harbor.game.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Environment;
import android.util.TypedValue;

import com.harbor.game.R;

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

    public static void buildDialog(DialogInterface.OnClickListener listener, Context ctx, String title, String message,
                                   String positiveButtonText, String negativeButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);



        builder.setPositiveButton(positiveButtonText, listener);

        if(negativeButtonText!=null){
            builder.setNegativeButton(negativeButtonText, listener);
        }

//        AlertDialog dialog = builder.create();
//
//        dialog.setContentView(R.layout.message_dialog);
//
//        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok);
//        okButton.setText(positiveButtonText);
//        okButton
//
//        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel);
//        if(negativeButtonText!=null){
//            cancelButton.setText(negativeButtonText);
//            cancelButton.setOnClickListener(listener);
//        }else{
//            cancelButton.setVisibility(View.INVISIBLE);
//        }
        AlertDialog dialog = builder.create();


        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

    }


}
