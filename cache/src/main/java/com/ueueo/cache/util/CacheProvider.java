package com.ueueo.cache.util;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;


public class CacheProvider extends ContentProvider {
    private static final String TAG = "CacheProvider";
    private static final String DATABASE_NAME = "CacheProvider.db";
    private static final String TABLE_NAME = "Cache_Provider_T";
    public static Uri URI = null;

    private ProviderDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new ProviderDBHelper(getContext(), DATABASE_NAME, null, 1);
        URI = Uri.parse("content://com.wbitech.medicine.cache");
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        try {
            count = mDBHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
        }
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try {
            long rowId = mDBHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
            if (rowId > 0) {
                Uri rowUri = ContentUris.withAppendedId(URI, rowId);
                getContext().getContentResolver().notifyChange(rowUri, null);
                return rowUri;
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mDBHelper.getReadableDatabase().query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        try {
            count = mDBHelper.getWritableDatabase().update(TABLE_NAME, values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
        }
        return count;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return DATABASE_NAME;
    }

    private static class ProviderDBHelper extends SQLiteOpenHelper {

        public ProviderDBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, schema TEXT,key TEXT,value TEXT,expiresIn INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
