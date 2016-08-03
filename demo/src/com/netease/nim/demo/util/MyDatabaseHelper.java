package com.netease.nim.demo.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bangzhu on 2016/7/29.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper{
    public static final String CREATE_MESSAGE = "create table message ("+
                                                    "messageId text primary key, "+
                                                    "isOpen integer)";
    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
