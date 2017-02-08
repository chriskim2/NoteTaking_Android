package com.example.donghyunkim.andr_final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donghyun Kim on 2016-12-14.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "FinalAssign2.db";
    public static final String TABLE_NAME = "memo_table";
    public static final String ID = "ID";
    public static final String COL_1 = "TITLE";
    public static final String COL_2 = "IMAGE_URI";
    public static final String COL_3 = "NOTE";
    public static final String COL_4 = "LAT";
    public static final String COL_5 = "LNG";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                          "TITLE TEXT, IMAGE_URI TEXT, NOTE TEXT, LAT REAL, LNG REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
        onCreate(db);
    }

    public List<Memo> getAllMemos() {
        SQLiteDatabase db = getReadableDatabase();
        List<Memo> mList = new ArrayList<Memo>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            Memo m = createMemoFromCursor(cursor);
            mList.add(m);
        }

        cursor.close();
        db.close();
        return mList;
    }

    public Memo getMemo(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Memo m = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where ID=" + id, null);

        if (cursor != null) {
            cursor.moveToFirst();
            m = createMemoFromCursor(cursor);
        }

        cursor.close();
        db.close();
        return m;
    }

    private Memo createMemoFromCursor(Cursor cursor) {
        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        String imgUri = cursor.getString(2);
        String note = cursor.getString(3);
        double lat = cursor.getDouble(4);
        double lng = cursor.getDouble(5);

        Memo m = new Memo(id, title, imgUri, note, lat, lng);

        return m;
    }

    public int size() {
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;

        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return result;
    }

    public boolean update(int id, String imgUri, String note, double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, imgUri);
        contentValues.put(COL_3, note);
        contentValues.put(COL_4, lat);
        contentValues.put(COL_5, lng);

        // updating row
        long result =  db.update(TABLE_NAME, contentValues, ID + " = ?",
                new String[] { String.valueOf(id) });

        db.close();

        return result == -1 ? false : true;
    }

    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " where ID=" + id);
        db.close();
    }

    public boolean insertMemo(String title, String imgUri, String note, double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (imgUri == null) {
            imgUri = "";
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, title);
        contentValues.put(COL_2, imgUri);
        contentValues.put(COL_3, note);
        contentValues.put(COL_4, lat);
        contentValues.put(COL_5, lng);
        long result = db.insert(TABLE_NAME, null, contentValues);

        db.close();

        return result == -1 ? false : true;
    }
}
