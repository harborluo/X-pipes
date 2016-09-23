package com.harbor.game.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.harbor.game.R;
import com.harbor.game.widget.SelectDialog;

public class HelpActivity extends Activity implements View.OnClickListener {

    private Button btn_test;

    public Integer[] pipeImages = {
            R.mipmap.cross,
            R.mipmap.right_down,
            R.mipmap.right_up,
            R.mipmap.left_down,
            R.mipmap.left_up,
            R.mipmap.vertical,
            R.mipmap.horizontal
    };

    ImageView imgView=null;
    TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        imgView = (ImageView) findViewById(R.id.pipe_image);
        btn_test = (Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(this);

        testTextView = (TextView)  findViewById(R.id.testTextView);

    }

    @Override
    public void onClick(View view) {


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i=0;
            public void run() {

                testTextView.setText("a"+i);
                imgView.setImageResource(pipeImages[i]);
                i++;
                if(i>pipeImages.length-1)
                {
                    return;
                }
                handler.postDelayed(this, 500);  //for interval...
            }
        };
        handler.postDelayed(runnable, 0); //for initial delay..

      //  verifyDialog("hello");


        SelectDialog selectDialog = new SelectDialog(this,R.style.dialog);//创建Dialog并设置样式主题
        Window win = selectDialog.getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.x = -80;//设置x坐标
        params.y = -60;//设置y坐标
        win.setAttributes(params);
        selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        selectDialog.show();

    }

//    private void verifyDialog(String msg)
//    {
//        final Dialog dialog = new Dialog(HelpActivity.this, R.style.dialog_translucent);
//        dialog.setContentView(R.layout.activity_help);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        TextView message = (TextView)dialog.getWindow().findViewById(R.id.testTextView);
//        Button okBtn = (Button)dialog.getWindow().findViewById(R.id.btn_test);
//        message.setText(msg);
//        okBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if(dialog!=null && dialog.isShowing())
//                {
//                    dialog.dismiss();
//                }
//            }
//        });
//        if(dialog!=null && !dialog.isShowing())
//        {
//            dialog.show();
//        }
//    }

}
