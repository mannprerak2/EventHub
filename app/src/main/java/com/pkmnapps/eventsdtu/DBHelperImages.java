package com.pkmnapps.eventsdtu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by prerak on 10/3/18.
 */

public class DBHelperImages extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "images.db";
    public static final String IMAGES_TABLE_NAME = "cacheimages";
    public static final String IMAGES_COLUMN_ID = "id";
    public static final String IMAGES_COLUMN_URL = "url";
    public static final String IMAGES_COLUMN_PATH = "path";
    public static final String IMAGES_COLUMN_DATE = "date";

    Context context;


    public DBHelperImages(Context context) {
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table cacheimages " +
                        "(id integer primary key, url text, path text, date integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS cacheimages");
        onCreate(db);
    }

    public boolean insertImage(String url,String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", url);
        contentValues.put("path", path);
        db.insert("cacheimages", null, contentValues);
        return true;
    }

    public boolean insertImage(String url, String path, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("url", url);
        contentValues.put("path", path);
        contentValues.put("date",Integer.parseInt((String) android.text.format.DateFormat.format("yyyyMMdd",date)));
        db.insert("cacheimages", null, contentValues);
        return true;
    }

    public Cursor getData(String url) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from cacheimages where url=\'"+url+"\'", null );
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, IMAGES_TABLE_NAME);
    }


    public Integer deleteImage(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(IMAGES_TABLE_NAME,
                "url = ? ",
                new String[] { url });
    }

    public ArrayList<String> getAllImages() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from cacheimages", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(IMAGES_COLUMN_URL)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
    public boolean isInImageDatabase(String url) {
        try {
            SQLiteDatabase sqldb = this.getReadableDatabase();
            String Query = "Select * from " + IMAGES_TABLE_NAME + " where " + IMAGES_COLUMN_URL + " = \'" + url + "\'";
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
    public boolean isPathInImageDatabase(String path) {
        try {
            SQLiteDatabase sqldb = this.getReadableDatabase();
            String Query = "Select * from " + IMAGES_TABLE_NAME + " where " + IMAGES_COLUMN_PATH + " = \'" + path + "\'";
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
    public void deleteImagesByTimeColumn(){
        String date;
        Date d = Calendar.getInstance().getTime();
        date = (String) android.text.format.DateFormat.format("yyyyMMdd",d);

        //delete image
        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(IMAGES_TABLE_NAME,"year <= ? AND month <= ? AND day < ?",new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(day)});
        String Query = "Select * from " + IMAGES_TABLE_NAME + " where "+IMAGES_COLUMN_DATE + " < \'" + date + "\'";
        Cursor cursor = db.rawQuery(Query,null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                String imageLink = cursor.getString(cursor.getColumnIndex(IMAGES_COLUMN_URL));
                //delete it from database
                deleteImage(imageLink);
                //delete it from cache directory
                File file = new File(context.getCacheDir(),imageLink);
                file.delete();
            } while (cursor.moveToNext());
            cursor.close();
        }


    }


}
