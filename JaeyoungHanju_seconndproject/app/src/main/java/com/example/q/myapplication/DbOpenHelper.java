package com.example.q.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {
    private static final String DATABASE_NAME = "ContactData.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBase.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DataBase.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }
    public long insertColumn( String name, String phoneNum , Integer favor){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateDB.NAME, name);
        values.put(DataBase.CreateDB.PHONENUMBER, phoneNum);
        values.put(DataBase.CreateDB.FAVOR, favor);
        return mDB.insert(DataBase.CreateDB._TABLENAME0, null, values);
    }
    public boolean updateColumn(long id, String name, String phoneNum , Integer favor){
        ContentValues values = new ContentValues();
        values.put(DataBase.CreateDB.NAME, name);
        values.put(DataBase.CreateDB.PHONENUMBER, phoneNum);
        values.put(DataBase.CreateDB.FAVOR, favor);
        return mDB.update(DataBase.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }
    public boolean deleteColumn(long id){
        return mDB.delete(DataBase.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }
}