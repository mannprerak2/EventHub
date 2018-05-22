package com.pkmnapps.eventsdtu;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by prerak on 10/3/18.
 */

public class DBHelperPin extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "pinned.db";
    public static final String PINNED_TABLE_NAME = "pinnedevents";
    public static final String PINNED_COLUMN_ID = "id";
    public static final String PINNED_COLUMN_NAME = "uniqueid";


    public DBHelperPin(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table pinnedevents " +
                        "(id integer primary key, uniqueid text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS pinnedevents");
        onCreate(db);
    }

    public boolean insertPinnedEvent (String uniqueid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uniqueid", uniqueid);
        db.insert("pinnedevents", null, contentValues);
        return true;
    }

    public Cursor getData(String uniqueid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from pinnedevents where uniqueid=\'"+uniqueid+"\'", null );
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PINNED_TABLE_NAME);
    }


    public Integer deletePinnedEvent (String uniqueid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("pinnedevents",
                "uniqueid = ? ",
                new String[] { uniqueid });
    }

    public ArrayList<String> getAllPinnedEvents() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from pinnedevents", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(PINNED_COLUMN_NAME)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
    public boolean isInPinnedDataBase(String uniqueid) {
        try {
            SQLiteDatabase sqldb = this.getReadableDatabase();
                           String Query = "Select * from " + PINNED_TABLE_NAME + " where " + PINNED_COLUMN_NAME + " = \'" + uniqueid + "\'";
                Cursor cursor = sqldb.rawQuery(Query, null);
                if (cursor.getCount() <= 0) {
                    cursor.close();
                    return false;
                }
                cursor.close();
                return true;
            }catch (Exception ignored){
        }
            return false;

    }

}
