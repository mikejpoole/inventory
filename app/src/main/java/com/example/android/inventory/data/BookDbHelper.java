package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    // DATABASE NAME AND VERSION
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ON CREATE
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL TO CREATE BOOK TABLE
        String SQL_CREATE_BOOK_TABLE =  "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, " +
                BookEntry.COLUMN_BOOK_PRICE + " TEXT, " +
                BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME + " TEXT, " +
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " TEXT" +
        ");";

        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    // ON UPGRADE
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There will be no future versions of this database but if there are I can drop and recreate the tables here
    }
}
