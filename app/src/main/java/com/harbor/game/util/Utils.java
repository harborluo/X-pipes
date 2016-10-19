package com.harbor.game.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.harbor.game.R;
import com.harbor.game.service.MusicService;
import com.harbor.game.widget.DialogButtonListener;
import com.harbor.game.widget.DialogMonitor;

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

            return  obj;

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void buildDialog(DialogInterface.OnClickListener listener, Context ctx, String title, String message,
                                   String... buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);


        if(buttonText.length==1){
            builder.setNeutralButton(buttonText[0],listener);
        }else if(buttonText.length==2){
            builder.setPositiveButton(buttonText[0],listener);
            builder.setNegativeButton(buttonText[1],listener);
        }

//        builder.setPositiveButton(positiveButtonText, listener);

//        if(negativeButtonText!=null){
//            builder.setNegativeButton(negativeButtonText, listener);
//        }

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

    public static void showDialog(Activity context, final DialogButtonListener listener,String message, String... buttonText){

        if(context instanceof DialogMonitor){
            DialogMonitor dialog = (DialogMonitor) context;
            if(dialog.isDialogPrompted()==true){
                return;
            }else{
                dialog.onShow();
            }
        }

        Log.d("Util", "showDialog: for activity :" + context.getLocalClassName());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.message_dialog,null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setCancelable(false);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        final Button okButton = (Button) view.findViewById(R.id.dialog_ok);

        TextView textView = (TextView) view.findViewById(R.id.dialog_message);
        textView.setText(message);

        okButton.setText(buttonText[0]);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.buttonClicked(okButton.getText().toString());
            }
        });

        final Button cancelButton = (Button) view.findViewById(R.id.dialog_cancel);
        cancelButton.setText(buttonText[1]);
        cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    listener.buttonClicked(cancelButton.getText().toString());
                }
            });

        dialog .getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

    }

    public static void startMusicService(Context ctx,int musicResourceId){
        Intent intent = new Intent(ctx, MusicService.class);
        intent.putExtra("music", R.raw.smooth_count_down);
        ctx.startService(intent);
    }


}
