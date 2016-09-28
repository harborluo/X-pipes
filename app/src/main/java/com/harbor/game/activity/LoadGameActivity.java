package com.harbor.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.harbor.game.GameData;
import com.harbor.game.R;
import com.harbor.game.util.Utils;
import com.harbor.game.widget.DialogButtonListener;
import com.harbor.game.widget.GameDataAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class LoadGameActivity extends Activity implements AdapterView.OnItemClickListener, DialogButtonListener {

    ListView listView = null;

    private String TAG ="LoadGameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

       initGameList();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initGameList();
    }

    private void initGameList(){
        String path = Utils.getDefaultFilePath();

        File dir = new File(path);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
               // file.delete();
               // return false;
                return file.getName().endsWith(".pipe");
            }
        });

        List<GameData> gamesList = new ArrayList<>(files.length);
        for(File f : files){
            GameData gameData = (GameData)  Utils.readObject(Utils.getDefaultFilePath() + File.separator + f.getName());
            gamesList.add(gameData);
        }

        GameDataAdapter adapter = new GameDataAdapter(LoadGameActivity.this, gamesList);

        listView.setAdapter(adapter);

        if(files.length==0){
//            Utils.buildDialog(this, this,"Notification","No saved games found.","OK");
            Utils.showDialog(this,this,"No saved games found.","Back","Start new game");
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(LoadGameActivity.this, "You Clicked at " +  position, Toast.LENGTH_SHORT).show();

        GameData game = (GameData) parent.getItemAtPosition(position);

        String fileName = game.getName();
        Intent intent = new Intent(LoadGameActivity.this, GameActivity.class);
        intent.putExtra("fileName",fileName);
        startActivity(intent);

    }

    @Override
    public void buttonClicked(String buttonText) {
        Log.i(TAG, "buttonClicked: "+buttonText);
        this.finish();
    }

}
