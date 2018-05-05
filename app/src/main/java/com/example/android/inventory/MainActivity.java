package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.android.inventory.data.BookContract.BookEntry;
import com.example.android.inventory.data.BookDbHelper;
import com.example.android.inventory.data.BookProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // DELETE ALL THE BOOKS SO WE START FROM SCRATCH
        deleteAllBooks();


        // INSERT EXAMPLE BOOKS TO GET US STARTED
        insertBook("The Hobbit","5.99",14,"Book Wholesale World","0131 123 4567");
        insertBook("Lord of the Rings","12.99",2,"Book Wholesale World","0131 123 4567");


        // LISTVIEW AND CURSOR ADAPTER TO CREATE LIST ITEM FOR EACH RECORD IN THE CURSOR
        ListView bookListView = (ListView) findViewById(R.id.list);
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);


        // INITIATE THE LOADER
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    public void insertBook(String title, String price, int quantity, String supplier, String phone){
        Log.i(LOG_TAG,"Attempting to insert " + title);

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, title);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME, supplier);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, phone);

        Uri myUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        if (myUri == null) {
            Log.w(LOG_TAG,getString(R.string.book_insert_failed));
        } else {
            Log.i(LOG_TAG,getString(R.string.book_insert_success));
        }
    }


    public void deleteAllBooks(){
        int iDeletedRows = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);

        Log.w(LOG_TAG, iDeletedRows + " rows deleted.");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_BOOK_TITLE, BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE, BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME, BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        // EXECUTE THE QUERY IN THE BACKGROUND
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
