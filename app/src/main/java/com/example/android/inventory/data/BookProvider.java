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

        // Figure out if the URI matcher can match the URI to a specific code
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


//    @Override
    public int update(Uri uri, ContentValues contentValues, String sel, String[] selArgs) {
        // NOT CALLED BUT WHEN IT IS REMEMBER THE OVERRIDE
        return 0;
    }


//    @Override
    public int delete(Uri uri, String sel, String[] selArgs) {
        SQLiteDatabase myDb = mDbHelper.getWritableDatabase();
        long id = myDb.delete(BookEntry.TABLE_NAME, sel, selArgs);
        return 0;
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
