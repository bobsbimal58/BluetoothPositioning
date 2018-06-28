package com.example.rohan.beaconmanual2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by WMCS on 2017-08-21.
 */

public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("");
        onCreate(db);
    }

    public void dropTable(){

    }

    public void insertData(String sql){
        SQLiteDatabase db;

        db = this.getWritableDatabase();

        db.execSQL("");
    }

    public Cursor selectData(String sql){
        Cursor cursor;
        SQLiteDatabase db;

        db = this.getReadableDatabase();

        cursor = db.rawQuery("", null);

        return cursor;
    }

}
