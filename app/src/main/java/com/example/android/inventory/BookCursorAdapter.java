package com.example.android.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.BookContract.BookEntry;

import static com.example.android.inventory.helper.BookImageHelper.loadImage;

public class BookCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static int iBooks = 0;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    // CREATE EMPTY LIST ITEM VIEW
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
    }


    // BIND THE DATA TO THE CORRECT PART OF THE LIST
    @Override
    public void bindView(View view, final Context context, Cursor c) {
        // THE ROW ID
        final int iRow = c.getInt(c.getColumnIndexOrThrow(BookEntry._ID));

        // BOOK TITLE
        TextView tvTitle = (TextView) view.findViewById(R.id.bookTitle);
        int titleColumnIndex = c.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        final String title = c.getString(titleColumnIndex);
        tvTitle.setText(title);

        // BOOK PRICE
        TextView tvPrice = (TextView) view.findViewById(R.id.bookPrice);
        int priceColumnIndex = c.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        String price = "Price: £" + c.getString(priceColumnIndex);
        tvPrice.setText(price);

        // QUANTITY
        TextView tvQuantity = (TextView) view.findViewById(R.id.bookQuantity);
        final int quantityColumnIndex = c.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        String quantity = "Quantity: " + c.getString(quantityColumnIndex);
        tvQuantity.setText(quantity);


        // SALE BUTTON: Task 2198: REDUCE QUANTITY BY ONE
        String sQty = c.getString(quantityColumnIndex);
        final int iQtyMinusOne = Integer.parseInt(sQty) - 1;

        Button btnSale = view.findViewById(R.id.btnSale);

        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityColumnIndex > 1 && iQtyMinusOne >= 0) {
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, iQtyMinusOne);
                    Uri mCurrentInventoryUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, iRow);
                    int rowsUpdated = context.getContentResolver().update(mCurrentInventoryUri, values, null, null);

                    if (rowsUpdated == 0) {
                        Toast.makeText(context.getApplicationContext(), "No rows were updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context.getApplicationContext(), "You have sold a copy of " + title, Toast.LENGTH_SHORT).show();
                    }
                } else if (iQtyMinusOne < 0) {
                    Toast.makeText(context.getApplicationContext(), "We have sold out of " + title, Toast.LENGTH_SHORT).show();
                }
            }
        });


        // SUPPLIER COMPANY AND PHONE NUMBER
        TextView tvSupplier = (TextView) view.findViewById(R.id.bookSupplier);
        int supplierCompanyNameColumnIndex = c.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_COMPANY_NAME);
        int supplierPhoneColumnIndex = c.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        String supplier = "Supplier: "
                + c.getString(supplierCompanyNameColumnIndex)
                + " ("
                + c.getString(supplierPhoneColumnIndex)
                + ")";
        tvSupplier.setText(supplier);


        // DOWNLOAD IMAGE USING MY GLIDE HELPER METHOD
        // Do not do this because not allowed by Udacity so must manually add the images to the app
//        loadImage(context,(ImageView) view.findViewById(R.id.bookImage));


        // ADD LOCALLY STORED IMAGES INSTEAD
        ImageView iv = (ImageView) view.findViewById(R.id.bookImage);
        int coverColumnIndex = c.getColumnIndex(BookEntry.COLUMN_BOOK_COVER);
        String sCoverColumnIndex = Integer.toString(coverColumnIndex);
        int resID;

        if (TextUtils.isEmpty(sCoverColumnIndex) || c.getString(coverColumnIndex) == null) {
            resID = 0;
        } else {
            Log.w(LOG_TAG,"Cover column index is " + c.getString(coverColumnIndex));
            resID = view.getResources().getIdentifier(c.getString(coverColumnIndex), "drawable", "com.example.android.inventory");
        }

        if (resID > 0) {
            iv.setImageResource(resID);
        } else {
            Drawable emptyBook = view.getResources().getDrawable(R.drawable.book_240);
            iv.setImageDrawable(emptyBook);
        }

    }

}
