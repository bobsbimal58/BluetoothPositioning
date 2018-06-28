package com.example.rohan.beaconmanual2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rohan on 7/5/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BeaconWMCS.db";
    public static final String TABLE_NAME = "BeaconData";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "X";
    public static final String COL_3 = "Y";
    public static final String COL_4 = "Beacon";
    public static final String COL_5 = "Rssi";







    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "create table " + TABLE_NAME + " (ID INTEGER, X INTEGER, Y INTEGER, Beacon TEXT, Rssi INTEGER)";
        db.execSQL(createTable);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXITS " + TABLE_NAME);
        this.onCreate(db);

    }

    public boolean insertData(int id, int x, int y, String beacon, int rssi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, x);
        contentValues.put(COL_3, y);
        contentValues.put(COL_4, beacon);
        contentValues.put(COL_5, rssi);


        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }







    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

}

