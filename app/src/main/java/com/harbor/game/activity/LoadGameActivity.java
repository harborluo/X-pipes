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
import com.harbor.game.util.DBHelper;
import com.harbor.game.util.Utils;
import com.harbor.game.widget.DialogButtonListener;
import com.harbor.game.widget.GameDataAdapter;

import java.util.List;

public class LoadGameActivity extends Activity implements AdapterView.OnItemClickListener, DialogButtonListener {

    ListView listView = null;

    private String TAG ="LoadGameActivity";

    DBHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        dbHelper = new DBHelper(this);
       initGameList();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initGameList();
    }

    private void initGameList(){
/*
        String path = Utils.getDefaultFilePath();
        File dir = new File(path);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".pipe");
            }
        });

        List<GameData> gamesList = new ArrayList<>(files.length);
        for(File f : files){
            GameData gameData = (GameData)  Utils.readObject(Utils.getDefaultFilePath() + File.separator + f.getName());
            //gamesList.add(gameData);
            gamesList.add(new GameData(gameData.toJson()));

            Log.d(TAG, "initGameList: " + gameData.toJson());
        }
*/

        List<GameData> gamesList = dbHelper.getAllGameData();

        GameDataAdapter adapter = new GameDataAdapter(LoadGameActivity.this, gamesList);

        listView.setAdapter(adapter);

        if(gamesList.size()==0){
            Utils.showDialog(this,this,getResources().getString(R.string.game_message_no_saved_game_found),
                    getResources().getString(R.string.game_text_button_start_new_game),
                    getResources().getString(R.string.game_text_dialog_button_back));
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(LoadGameActivity.this, "You Clicked at " +  position, Toast.LENGTH_SHORT).show();

        GameData game = (GameData) parent.getItemAtPosition(position);

        Intent intent = new Intent(LoadGameActivity.this, GameActivity.class);
        //String fileName = game.getName();
        //intent.putExtra("fileName",fileName);
        intent.putExtra("gameData",game);
        startActivity(intent);

    }

    @Override
    public void buttonClicked(String buttonText) {
        Log.i(TAG, "buttonClicked: "+buttonText);
        this.finish();
        if((buttonText.equals(getResources().getString(R.string.game_text_button_start_new_game)))){
            Intent intent = new Intent();
            intent.setAction("game");
            startActivity(intent);
        }
    }

}
