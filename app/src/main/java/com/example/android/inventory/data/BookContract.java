package com.example.android.inventory.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

public final class BookContract {

    // EMPTY CONSTRUCTOR
    private BookContract() {}

    // CONTENT AUTHORITY
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    // BASE URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // THE PATHS
    public static final String PATH_BOOK = "book";
    public static final String PATH_SUPPLIER = "supplier";

    // CONSTANT VALUES - BOOKS
    public static final class BookEntry implements BaseColumns {

        // CONTENT URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOK);

        // MIME TYPE - FOR A LIST
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        // MIME TYPE - FOR A SINGLE ROW
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        // THE BOOK TABLE
        public final static String TABLE_NAME = "tblBook";

        // THE COLUMNS IN THE BOOK TABLE
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_TITLE = "title";
        public final static String COLUMN_BOOK_PRICE = "dPrice";
        public final static String COLUMN_BOOK_QUANTITY = "iQuantity";
        public final static String COLUMN_BOOK_COVER = "cover";
        public final static String COLUMN_BOOK_SUPPLIER_COMPANY_NAME = "supplier_company_name";
        public final static String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone";
    }


    // CONSTANT VALUES - SUPPLIERS
    // TODO: I want to make this relational so set up values for tblSupplier here

}

