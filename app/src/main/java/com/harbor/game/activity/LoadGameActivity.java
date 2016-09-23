package com.harbor.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harbor.game.R;
import com.harbor.game.util.Utils;

import java.io.File;
import java.io.FileFilter;

public class LoadGameActivity extends Activity implements View.OnClickListener{

    LinearLayout layout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        String path = Utils.getDefaultFilePath();

        File dir = new File(path);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {

                return file.getName().endsWith(".pipe");
            }
        });

        layout =(LinearLayout) findViewById(R.id.gameFilesContainer);

        for(File f : files){

            TextView view = new TextView(this);
            view.setText(f.getName());
            view.setOnClickListener(this);
            layout.addView(view);

//            f.delete();
        }

//        layout.addView();

//        Toast.makeText(this, "Count of game data file = "+ files.length, Toast.LENGTH_LONG).show();

    }


    @Override
    public void onClick(View view) {
        String fileName = ((TextView)view).getText().toString();
        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra("fileName",fileName);
        startActivity(intent);
    }
}
