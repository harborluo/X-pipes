package com.harbor.game.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.harbor.game.GameData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harbor on 11/21/2016.
 */
public class DBHelper  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "x_pipe.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS person" +
//                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, age INTEGER, info TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS game_data(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, json_data text)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
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
        if(game.getId()==-1){

           db.execSQL("insert into game_data(name,json_data) values (?,?)",new Object[]{ game.getName(),game.toJson()});
        }else{
           db.execSQL("update game_data set json_data = ? where id = ?",new Object[]{ game.toJson(),game.getId()});
        }
//        db.endTransaction();
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
}