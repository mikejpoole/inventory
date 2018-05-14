package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventory.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    // URI MATCHER
    private static final int BOOKS = 100;       // Multiple Books
    private static final int BOOK_ID = 101;     // Single Book
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // STATIC INITIALIZER
    static {
        // THE ADDURI PATTERNS THAT THE PROVIDER SHOULD RECOGNISE
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOK, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOK + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String order) {

        // GET DATABASE AND CREATE A CURSOR
        SQLiteDatabase myDb = mDbHelper.getReadableDatabase();
        Cursor c;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                c = myDb.query(BookEntry.TABLE_NAME, proj, sel, selArgs,null, null, order);
                break;
            case BOOK_ID:
                sel = BookEntry._ID + "=?";
                selArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                c = myDb.query(BookEntry.TABLE_NAME, proj, sel, selArgs,null, null, order);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // SET CURSOR NOTIFICATION URI AND RETURN THE CURSOR
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    // INSERT A BOOK (CALLED BY INSERT METHOD ABOVE)
    private Uri insertBook(Uri uri, ContentValues values) {

        // VALIDATION
        String name = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
        if (name == null) {
            throw new IllegalArgumentException("The book requires a title");
        }

        // INSERT VALUES INTO DATABASE
        SQLiteDatabase myDb = mDbHelper.getWritableDatabase();
        long id = myDb.insert(BookEntry.TABLE_NAME, null, values);

        // CHECK IT INSERTED AND IF NOT THEN RETURN EARLY
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } else {
            Log.i(LOG_TAG, "Inserted row for " + uri);
        }

        // DATA HAS CHANGED SO INFORM LISTENERS
        getContext().getContentResolver().notifyChange(uri, null);

        // RETURN THE URI WITH THE NEW ROWID
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // VALIDATION
        if (values.containsKey(BookEntry.COLUMN_BOOK_TITLE)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("The book requires a title");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            String price = values.getAsString(BookEntry.COLUMN_BOOK_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("The book requires a price");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("The book requires a quantity");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME)) {
            String company = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME);
            if (company == null) {
                throw new IllegalArgumentException("The book requires a supplier name");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)) {
            String phone = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("The book requires the phone number of the supplier");
            }
        }

        // Only proceed if new values to be updated
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
