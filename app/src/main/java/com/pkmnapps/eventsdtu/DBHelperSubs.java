package com.pkmnapps.eventsdtu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by prerak on 13/3/18.
 */

public class DBHelperSubs extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "subscribed.db";
    public static final String SUBS_TABLE_NAME = "subssociety";
    public static final String SUBS_COLUMN_ID = "id";
    public static final String SUBS_COLUMN_UNIQUEID = "uniqueid";
    public static final String SUBS_COLUMN_NAME = "name";
    public static final String SUBS_COLUMN_IMAGE = "image";


    public DBHelperSubs(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table subssociety " +
                        "(id integer primary key, uniqueid text, name text, image text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS subssociety");
        onCreate(db);
    }

    public boolean insertSubsScoiety (String uniqueid, String name, String imageLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uniqueid", uniqueid);
        contentValues.put("name", name);
        contentValues.put("image", imageLink);
        db.insert("subssociety", null, contentValues);
        return true;
    }

    public Cursor getData(String uniqueid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from subssociety where uniqueid=\'"+uniqueid+"\'", null );
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, SUBS_TABLE_NAME);
    }


    public Integer deleteSubsSociety (String uniqueid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("subssociety",
                "uniqueid = ? ",
                new String[] { uniqueid });
    }

    public ArrayList<String[]> getAllSubsSociety() {//0 is uniqueid 1 is name 2 is image
        ArrayList<String[]> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from subssociety", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            String[] a = new String[]{res.getString(res.getColumnIndex(SUBS_COLUMN_UNIQUEID)),res.getString(res.getColumnIndex(SUBS_COLUMN_NAME)),res.getString(res.getColumnIndex(SUBS_COLUMN_IMAGE))};
            array_list.add(a);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
    public boolean isInSubsDataBase(String uniqueid) {
        try {
            SQLiteDatabase sqldb = this.getReadableDatabase();
            String Query = "Select * from " + SUBS_TABLE_NAME + " where " + SUBS_COLUMN_UNIQUEID + " = \'" + uniqueid + "\'";
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

