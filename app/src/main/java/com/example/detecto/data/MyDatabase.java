package com.example.detecto.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.example.detecto.MainActivity;

import java.util.ArrayList;

public class MyDatabase {
    Context context;
    SQLiteOpenHelper dbHelper;

    public MyDatabase(Context c){
        context=c;
        dbHelper=new MyDBHelper(context);
    }

    public MainActivity.DBResult getSearchRes(String q){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT _id,Title,Description FROM DETECTEDT WHERE Title LIKE '%"+q+"%' OR Description Like '%"+q+"%' ORDER BY _id DESC",null);//db.query("DETECTEDT",new String[]{"_id","Title","Description"},null,null,null,null,"_id DESC");
        ArrayList<Integer> ids=new ArrayList<>();
        ArrayList<String> Titles=new ArrayList<>();
        ArrayList<String> body=new ArrayList<>();
        if(cursor.moveToFirst()) {
            ids.add(Integer.valueOf(cursor.getInt(0)));
            Titles.add(cursor.getString(1));
            body.add(cursor.getString(2));
            while (cursor.moveToNext()) {
                ids.add(cursor.getInt(0));
                Titles.add(cursor.getString(1));
                body.add(cursor.getString(2));
            }
        }
        db.close();
        MainActivity.DBResult dbr=new MainActivity.DBResult(ids,Titles,body);
        return dbr;
    }

    public void deleteFromDB(int id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete("DETECTEDT","_id=?",new String[]{Integer.toString(id)});
        db.close();
    }

    public void writeToDB(String Title,String body){
        if(TextUtils.isEmpty(Title)||TextUtils.isEmpty(body)){
            return;
        }
        new MyasyncTask().execute(new asyncTaskParams(Title,body));
    }

    public static class asyncTaskParams{
        String title;
        String body;
        asyncTaskParams(String t,String b){
            title=t;
            body=b;
        }
    }

    private class MyasyncTask extends AsyncTask<asyncTaskParams,Void,Void>{
        @Override
        protected Void doInBackground(asyncTaskParams... asyncTaskParams) {
            SQLiteDatabase db=dbHelper.getWritableDatabase();
            ContentValues cv=new ContentValues();
            cv.put("Title",asyncTaskParams[0].title);
            cv.put("Description",asyncTaskParams[0].body);

            db.insert("DETECTEDT",null,cv);
            db.close();
            return null;
        }
    }

}
