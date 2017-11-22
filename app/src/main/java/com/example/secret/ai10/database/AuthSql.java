package com.example.secret.ai10.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by Secret on 2017/4/18.
 */

public class AuthSql extends SQLiteOpenHelper {


    public AuthSql(Context context) {
        super(context, "database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Auth(" +
                "name TEXT ,isSelected INTEGER ,"+
                "deviceName TEXT)");
    }

    public void saveContent(Auth auth){

        SQLiteDatabase dbWrite = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",auth.getName());
        cv.put("isSelected",auth.isSelected());
        cv.put("deviceName",auth.getDeviceName());
//        cv.put("isOpen",customMode.isOpen());

        dbWrite.insert("Auth",null,cv);
        dbWrite.close();

        System.out.println("======saving...");



    }

    public void queryContent(List<Auth> list,String deviceName){
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;
        Cursor cursor = db.query("Auth",null,"deviceName = ?",new String[]{deviceName},null,null,null);
        while (cursor.moveToNext()){
            Auth auth = new Auth();
            String name = cursor.getString(0);
            auth.setName(name);
            list.add(auth);

            count++;
        }
        System.out.println("======query:"+count);
    }


//    public void updateContent(CustomMode customMode,String title){
//        SQLiteDatabase dbWrite = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put("title",customMode.getTitle());
//        cv.put("content",customMode.getContent());
//        cv.put("time",customMode.getTime());
//        dbWrite.update("Memos",cv,"title=?",new String[]{title});
//        dbWrite.close();
//    }

    public void deleteContent(String title){
        SQLiteDatabase dbWrite = getWritableDatabase();
        dbWrite.delete("Auth","name=?",new String[]{title});
        dbWrite.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
