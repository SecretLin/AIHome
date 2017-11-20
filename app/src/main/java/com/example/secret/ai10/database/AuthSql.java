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
                "name TEXT DEFAULT NONE)");
    }

    public void saveContent(Auth auth){

        SQLiteDatabase dbWrite = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",auth.getName());
//        cv.put("isOpen",customMode.isOpen());

        dbWrite.insert("Auth",null,cv);
        dbWrite.close();



    }

    public void queryContent(List<Auth> list){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("Auth",null,null,null,null,null,null);
        if (cursor.moveToNext()){
            Auth auth = new Auth();
            String name = cursor.getString(0);
            auth.setName(name);
            list.add(auth);
        }
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
