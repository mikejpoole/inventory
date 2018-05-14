package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventory.data.BookContract.BookEntry;

import junit.framework.Test;

public class BookEdit extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = BookEdit.class.getSimpleName();

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;

    private EditText etTitle;
    private EditText etPrice;
    private EditText etQuantity;
    private EditText etSupplier;
    private EditText etPhone;

    private String sPhone;

    // LISTEN FOR TOUCHES SO WE CAN IMPLY IF THEY HAVE UPDATED THE BOOK
    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);

        // GET URI FROM INTENT SO WE KNOW WHICH BOOK WE ARE EDITING OR IF WE ARE CREATING A NEW BOOK
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            // NEW BOOK
            setTitle(getString(R.string.title_new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
//            invalidateOptionsMenu();
        } else {
            // EXISTING BOOK SO GET DATA FROM DATABASE
            setTitle(getString(R.string.title_edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        etTitle = (EditText) findViewById(R.id.editTitle);
        etPrice = (EditText) findViewById(R.id.editPrice);
        etQuantity = (EditText) findViewById(R.id.editQuantity);
        etSupplier = (EditText) findViewById(R.id.editSupplier);
        etPhone = (EditText) findViewById(R.id.editPhone);

        etTitle.setOnTouchListener(mTouchListener);
        etPrice.setOnTouchListener(mTouchListener);
        etQuantity.setOnTouchListener(mTouchListener);
        etSupplier.setOnTouchListener(mTouchListener);
        etPhone.setOnTouchListener(mTouchListener);
    }



    // SAVE THE BOOK INFORMATION
    private void saveBook() {
        String sTitle = etTitle.getText().toString().trim();
        String sPrice = etPrice.getText().toString().trim();
        String sQuantity = etQuantity.getText().toString().trim();
        String sSupplier = etSupplier.getText().toString().trim();
        String sPhone = etPhone.getText().toString().trim();


        // RETURN EARLY IF NEW OR BLANK BOOK
        if (mCurrentBookUri == null && TextUtils.isEmpty(sTitle) && TextUtils.isEmpty(sPrice) && TextUtils.isEmpty(sQuantity)) {
            return;
        }


        // VALIDATION OF REQUIRED FIELDS
        if (TextUtils.isEmpty(sTitle) || TextUtils.isEmpty(sPrice) || TextUtils.isEmpty(sQuantity) || TextUtils.isEmpty(sSupplier) || TextUtils.isEmpty(sPhone)){
            Toast.makeText(this, "You must enter text in all the fields.", Toast.LENGTH_SHORT).show();
            Log.w(LOG_TAG,"Validation failed.");
        } else {
            ContentValues cv = new ContentValues();
            cv.put(BookEntry.COLUMN_BOOK_TITLE, sTitle);
            cv.put(BookEntry.COLUMN_BOOK_PRICE, sPrice);

            // QUANTITY
            int iQuantity;

            if (TextUtils.isEmpty(sQuantity)) {
                // Make it zero by default
                iQuantity = 0;
            } else {
                // If it exists try to make it an integer...
                try {
                    iQuantity = Integer.parseInt(sQuantity);
                    Log.i(LOG_TAG,iQuantity + " is a number");

                    // ...and only save it if it is positive
                    if (iQuantity >= 0){
                        cv.put(BookEntry.COLUMN_BOOK_QUANTITY, iQuantity);
                    } else {
                        Toast.makeText(this, "Quantity cannot be negative", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, sQuantity + " is not a number", Toast.LENGTH_SHORT).show();
                }
            }


            cv.put(BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME, sSupplier);
            cv.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, sPhone);

            if (mCurrentBookUri == null) {
                // INSERT A NEW BOOK
                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, cv);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.insert_book_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_book_success), Toast.LENGTH_SHORT).show();
                }
            } else {
                // PASS THE NEW CONTENTVALUES TO UPDATE AN EXISTING BOOK
                int rowsAffected = getContentResolver().update(mCurrentBookUri, cv, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.update_book_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.update_book_success), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    // THE MANY MENU METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_edit, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_order:
                orderBook();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // UP NAVIGATION
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(BookEdit.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(BookEdit.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // WARN USER ABOUT UNSAVED CHANGES
       DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_COVER,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE
        };

        return new CursorLoader(this, mCurrentBookUri, projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // FINISH EARLY IF NO RESULTS
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            String title = cursor.getString(titleColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            final String phone = cursor.getString(phoneColumnIndex);

            etTitle.setText(title);
            etPrice.setText(price);
            etQuantity.setText(Integer.toString(quantity));
            etSupplier.setText(supplier);
            etPhone.setText(phone);


            // ALSO SAVE PHONE TO VARIABLE TO USE FOR THE DIALER INTENT
            sPhone = phone;


            // UPDATE BOOK COVER
            // TODO = Add some validation if no image exists
            ImageView iv = (ImageView) findViewById(R.id.bookCover);
            int coverColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_COVER);
            String sCoverColumnIndex = Integer.toString(coverColumnIndex);
            int resID;

            if (TextUtils.isEmpty(sCoverColumnIndex) || cursor.getString(coverColumnIndex) == null) {
                resID = 0;
            } else {
                Log.w(LOG_TAG,"Cover column index is " + cursor.getString(coverColumnIndex));
                resID = getResources().getIdentifier(cursor.getString(coverColumnIndex), "drawable", "com.example.android.inventory");
            }

            if (resID > 0) {
                iv.setImageResource(resID);
            } else {
                Drawable emptyBook = getResources().getDrawable(R.drawable.book_240);
                iv.setImageDrawable(emptyBook);
            }


            // LISTENERS TO INCREMENT AND DECREMENT STOCK
            Button btnPlus = findViewById(R.id.btnPlus);
            btnPlus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    increment(v);
                }
            });

            Button btnMinus = findViewById(R.id.btnMinus);
            btnMinus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    decrement(v);
                }
            });
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        etTitle.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        etSupplier.setText("");
        etPhone.setText("");
    }


    // UNSAVED CHANGES DIALOG
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);

        // User wants to continue editing
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // USER CONFIRMATION OF DELETION
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);

        // User confirmed deletion
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });

        // User cancelled deletion
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // DELETE BOOK FROM DATABASE IF IT EXISTS
    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_book_success), Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }


    // INCREMENT AND DECREMENT STOCK
    public void increment(View view) {
        etQuantity = (EditText) findViewById(R.id.editQuantity);
        int iQty = Integer.valueOf(etQuantity.getText().toString());
        iQty++;

        etQuantity.setText(Integer.toString(iQty));
    }

    public void decrement(View view) {
        etQuantity = (EditText) findViewById(R.id.editQuantity);
        int iQty = Integer.valueOf(etQuantity.getText().toString());

        if (iQty==0) {
            Toast.makeText(this, "You cannot have less than zero books you silly billy.", Toast.LENGTH_SHORT).show();
            return;
        }

        iQty--;
        etQuantity.setText(Integer.toString(iQty));
    }

    // LAUNCH DIALER INTENT TO ORDER BOOK FROM SUPPLIER
    public void orderBook() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", sPhone, null)));
    }

}
