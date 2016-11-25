package com.harbor.game.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.harbor.game.GameData;
import com.harbor.game.GameLevelConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harbor on 11/21/2016.
 */
public class DBHelper  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "x_pipe.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS game_data");
        db.execSQL("DROP TABLE IF EXISTS game_level");

        db.execSQL("CREATE TABLE IF NOT EXISTS game_data(id INTEGER PRIMARY KEY AUTOINCREMENT, mode VARCHAR, name VARCHAR, json_data text)");

        db.execSQL("CREATE TABLE IF NOT EXISTS game_level(level INTEGER PRIMARY KEY, is_locked VARCHAR, highestScore INTEGER)");
        db.beginTransaction();
        for(int i=0;i<25;i++){
            db.execSQL("insert into game_level(level,is_locked,highestScore) values(?,?,?)", new Object[]{i+1,i==0?"N":"Y",0});
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
        onCreate(db);
    }

    public List<GameData> getAllGameData() {
        List<GameData> games = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("game_data",new String[]{"id","json_data"},"",new String[]{},"","","");
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String jsonData = cursor.getString(1);
            GameData data = new GameData(jsonData);
            data.setId(id);
            games.add(data);
        }
        cursor.close();
        return games;
    }

    public void saveGame(GameData game){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

//        if("Level".equals(game.getMode())){
            if(game.isPassed()){
                //unlock next level
                db.execSQL("update game_level set is_locked = ? where level= ? ",new Object[]{ "N", game.getLevel()+1});
            }else if(game.getLevel()>1){
                db.execSQL("update game_level set is_locked = ? where level= ? ",new Object[]{ "N", game.getLevel()                         });
            }
//        }

        if(game.getId()==-1){
           db.execSQL("insert into game_data(name,json_data) values (?,?)",new Object[]{ game.getName(),game.toJson()});
        }else{
           db.execSQL("update game_data set json_data = ? where id = ?",new Object[]{ game.toJson(),game.getId()});
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void removeGame(Object... args){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        if(args.length==0){
           db.execSQL("delete from game_data");

        }else{
            db.execSQL("delete from game_data where id = ?", args);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<GameLevelConfig> getAllGameLevel() {
        List<GameLevelConfig> list = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query("game_level",new String[]{"level","is_locked","highestScore"},"",new String[]{},"","","level asc");
        while(cursor.moveToNext()){
            int level = cursor.getInt(0);
            boolean locked = "Y".equals(cursor.getString(1));
            int score = cursor.getInt(2);

            GameLevelConfig config = new GameLevelConfig(level,locked,score);

            list.add(config);

        }
        cursor.close();

        return list;
    }
}