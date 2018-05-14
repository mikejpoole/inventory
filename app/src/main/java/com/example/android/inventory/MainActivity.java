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


        // SHOW THE LIST BY DEFAULT
        // This is working for manually toggling between the bookcase and list
        showList();
//        hideList();


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


    // SALE BUTTON LISTENER
    private View.OnClickListener myButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);

            Toast.makeText(MainActivity.this, "The row clicked on was " + position, Toast.LENGTH_SHORT).show();
        }
    };


    public void sellBook(View v){
        // TODO: Make it work with correct quantity
        // http://androidforbeginners.blogspot.co.uk/2010/03/clicking-buttons-in-listview-row.html
        int iQty = 99;

        Toast.makeText(this, "Selling book..." , Toast.LENGTH_SHORT).show();

        ListView lvItems = (ListView) findViewById(R.id.list);

        for (int i=0; i < lvItems.getChildCount(); i++)
        {
            Log.i(LOG_TAG,"Row " + i);
//            lvItems.getChildAt(i).setBackgroundColor(Color.BLUE);
        }
//
//        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

//        TextView child = (TextView)vwParentRow.getChildAt(0);
//        Button btnChild = (Button)vwParentRow.getChildAt(1);
//        btnChild.setText(child.getText());
//        btnChild.setText("I've been clicked!");


//        ContentValues values = new ContentValues();
//        values.put(BookEntry.COLUMN_BOOK_QUANTITY, iQty);

            Log.i(LOG_TAG,"Selling a book...");

//        Uri myUri = getContentResolver().update(BookEntry.CONTENT_URI, null, null);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_hobbit:
                insertBook("The Hobbit","5.99",14, "the_hobbit_100","BookWorld","0131 123 4567");
                return true;
            case R.id.insert_lotr:
                insertBook("The Fellowship of the Ring","12.99",2,"lord_of_the_rings_1_100","BookWorld","0131 123 4567");
                insertBook("The Two Towers","12.99",2,"lord_of_the_rings_2_100","BookWorld","0131 123 4567");
                insertBook("The Return of the King","12.99",2,"lord_of_the_rings_3_100","BookWorld","0131 123 4567");
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showList(){
        ListView bookListView = findViewById(R.id.list);
        bookListView.setVisibility(View.VISIBLE);

        View vEmpty = findViewById(R.id.empty_view);
        vEmpty.setVisibility(View.INVISIBLE);
    }

    public void hideList() {
        ListView bookListView = findViewById(R.id.list);
        bookListView.setVisibility(View.INVISIBLE);

        View vEmpty = findViewById(R.id.empty_view);
        vEmpty.setVisibility(View.VISIBLE);
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

//        Toast.makeText(this, "Loading has finished", Toast.LENGTH_SHORT).show();

        // OPEN A CURSOR TO COUNT WITH
//        Cursor c = mCursorAdapter.getCursor();


        // HIDE LISTVIEW IF EMPTY
//        Toast.makeText(this, "There are " + c.getCount() + " books", Toast.LENGTH_SHORT).show();

//        if (c.getCount() > 0) {
//            showList();
//        } else {
//            hideList();
//        }

//        c.close();


        // SALE BUTTON LISTENER
        // TODO = only listen if there are results to listen to
        Button btnSale = findViewById(R.id.btnSale);

        if (btnSale != null) {
            btnSale.setOnClickListener(myButtonClickListener);
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

//        Toast.makeText(this, "Loading has reset", Toast.LENGTH_SHORT).show();

    }

}
