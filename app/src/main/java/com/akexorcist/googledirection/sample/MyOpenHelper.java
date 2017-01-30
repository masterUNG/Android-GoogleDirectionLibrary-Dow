package com.akexorcist.googledirection.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by masterUNG on 1/30/2017 AD.
 */

public class MyOpenHelper extends SQLiteOpenHelper{

    //Explicit
    private Context context;
    public static final String database_name = "dow.db";
    private static final int database_version = 1;

    private static final String userTABLE_detail = "create table userTABLE (" +
            "_id integer primary key," +
            "OriginName text," +
            "OriginLat text," +
            "OriginLng text," +
            "DestinationName text," +
            "DestinationLat text," +
            "DestinationLng text," +
            "Way text);";

    public MyOpenHelper(Context context) {
        super(context, database_name, null, database_version);
        this.context = context;
    }   // Constructor

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(userTABLE_detail);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}   // Main Class
