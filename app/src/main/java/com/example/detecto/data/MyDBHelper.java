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
        db.execSQL("INSERT INTO DETECTEDT(Title,Description) VALUES ('TAKING NOTE','Click on + icon.'),('SEARCH','Click on search and type the keyword.'),('DETECT TEXT','Click on Read from Image for detecting from a pre-existing image.\n" +
                "\n" +
                "Click on Read using camera for detecting through camera capture.')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    MyDBHelper(Context c){
        super(c,DB_NAME,null,DB_VERSION);
    }
}
