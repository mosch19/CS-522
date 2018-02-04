package com.example.mosch.eclipsebattlesim;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mosch on 9/15/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "eclipse.db";
    public static final String TABLE_NAME = "components";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Name";
    public static final String COL_3 = "Hull";
    public static final String COL_4 = "Comp";
    public static final String COL_5 = "Shield";
    public static final String COL_6 = "Init";
    public static final String COL_7 = "Power";
    public static final String COL_8 = "Missile";
    public static final String COL_9 = "Dice";
    public static final String COL_10 = "Damage";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, HULL INTEGER, COMP INTEGER, SHIELD INTEGER, INIT INTEGER, POWER INTEGER, MISSILE BOOLEAN, DICE INTEGER, DAMAGE INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
