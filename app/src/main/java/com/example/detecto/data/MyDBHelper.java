package com.example.detecto.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="textDB";
    private static final int DB_VERSION=1;
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE DETECTEDT("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"Title TEXT,"
                +"Description TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    MyDBHelper(Context c){
        super(c,DB_NAME,null,DB_VERSION);
    }
}
