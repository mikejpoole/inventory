package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.android.inventory.data.BookContract.BookEntry;

import static com.example.android.inventory.BookCursorAdapter.iBooks;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // LISTVIEW AND CURSOR ADAPTER TO CREATE LIST ITEM FOR EACH RECORD IN THE CURSOR
        ListView bookListView = (ListView) findViewById(R.id.list);

        // DISPLAY THE EMPTY VIEW IF THERE ARE NO BOOKS INT HE DATABASE
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);


        // INITIATE THE LOADER
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        // LISTVIEW LISTENER CREATES THE URI, SET IT ON THE DATA FIELD OF THE INTENT AND LAUNCH THE EDITOR
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, BookEdit.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        // FAB LISTENER
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookEdit.class);
                startActivity(intent);
            }
        });

    }


    public void insertBook(String title, String price, int quantity, String cover, String supplier, String phone){
        Log.i(LOG_TAG,"Attempting to insert " + title);

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, title);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_COVER, cover);
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

        if (iDeletedRows == 0) {
            Toast.makeText(this, "There were no books to be deleted.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "All " + iDeletedRows + " books have been deleted.", Toast.LENGTH_SHORT).show();
        }
    }


    // INFLATE THE OPTIONS MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    // INSERT PRESET BOOKS OR DELETE ALL BOOKS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_hobbit:
                insertBook("The Hobbit","5.99",14, "the_hobbit_100","BookWorld","0131 123 4567");
                return true;
            case R.id.insert_lotr:
                insertBook("The Fellowship of the Ring","12.99",14,"lord_of_the_rings_1_100","BookWorld","0131 123 4567");
                insertBook("The Two Towers","14.99",9,"lord_of_the_rings_2_100","Rare Books","0207 123 1231");
                insertBook("The Return of the King","16.99",3,"lord_of_the_rings_3_100","BookWorld","0131 123 4567");
                return true;
            case R.id.insert_sharepoint:
                insertBook("SharePoint Designer","12.95",5, "sharepoint","Mike Poole","0131 666 2555");
                return true;
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookEntry._ID, BookEntry.COLUMN_BOOK_TITLE, BookEntry.COLUMN_BOOK_QUANTITY, BookEntry.COLUMN_BOOK_COVER,
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
