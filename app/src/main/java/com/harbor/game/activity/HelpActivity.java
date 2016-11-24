package com.harbor.game.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.harbor.game.R;
import com.harbor.game.util.DBHelper;
import com.harbor.game.util.Utils;

import java.io.File;
import java.io.FileFilter;

public class HelpActivity extends Activity implements View.OnClickListener {

    private Button btn_test;

    DBHelper dbHelper = null;

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

        dbHelper = new DBHelper(this);

        //TODO add game help tutorial here

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

    }

    public void cleanAllSavedGames(View view){

        String path = Utils.getDefaultFilePath();

        File dir = new File(path);

        dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.getName().endsWith(".pipe")){
                    file.delete();
                }

                return false;
            }
        });

        dbHelper.removeGame();

    }

}
