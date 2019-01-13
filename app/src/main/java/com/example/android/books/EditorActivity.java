/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.books;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.books.data.BookContract;
import com.example.android.books.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQtyEditText;
    private EditText mSupEditText;
    private EditText mPhoneEditText;
    private Uri mPhoneUri;


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
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));


            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQtyEditText = (EditText) findViewById(R.id.edit_book_qty);
        mSupEditText = (EditText) findViewById(R.id.edit_book_sup);
        mPhoneEditText = (EditText) findViewById(R.id.edit_book_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQtyEditText.setOnTouchListener(mTouchListener);
        mSupEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);

        Button increaseButton = (Button) findViewById(R.id.button_quantity_plus_one);
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemChange(true);
                mBookHasChanged = true;
            }
        });

        // Button to decrease quantity
        Button decreaseButton = (Button) findViewById(R.id.button_quantity_minus_one);
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemChange(false);
                mBookHasChanged = true;
            }
        });

    }

    private void itemChange(boolean change) {
        String oldQtyString = mQtyEditText.getText().toString();
        int oldQty;

        if(change) {
            if (oldQtyString.isEmpty()) {
                oldQty = 0;
            } else {
                oldQty = Integer.parseInt(oldQtyString);
            }
            mQtyEditText.setText(String.valueOf(oldQty + 1));
        }else {

            if (oldQtyString.isEmpty()) {
                return;
            } else if (oldQtyString.equals("0")) {
                return;
            } else {
                oldQty = Integer.parseInt(oldQtyString);
                mQtyEditText.setText(String.valueOf(oldQty - 1));
            }
        }
    }


    private void saveBook() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String qtyString = mQtyEditText.getText().toString().trim();
        String supString = mSupEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();


        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(supString) &&
                TextUtils.isEmpty(phoneString)) {
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            return;
        }
        if (TextUtils.isEmpty(qtyString)) {
            return;
        }
        if (TextUtils.isEmpty(supString)) {
            return;
        }
        if (TextUtils.isEmpty(phoneString)) {
            return;
        }



        double price = Double.parseDouble(priceString);

        ContentValues values = new ContentValues();
        values.put(BookEntry.BOOK_NAME, nameString);
        values.put(BookEntry.BOOK_PRICE, price);
        values.put(BookEntry.SUPPLIER_NAME, supString);
        values.put(BookEntry.BOOK_PHONE, phoneString);

        int qty = 0;
        if (!TextUtils.isEmpty(qtyString)) {
            qty = Integer.parseInt(qtyString);
        }
        values.put(BookEntry.BOOK_QUANTITY, qty);

        if (mCurrentBookUri == null) {

            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:

                String nameString = mNameEditText.getText().toString().trim();
                String priceString = mPriceEditText.getText().toString().trim();
                String qtyString = mQtyEditText.getText().toString().trim();
                String supString = mSupEditText.getText().toString().trim();
                String phoneString = mPhoneEditText.getText().toString().trim();

                if (nameString.isEmpty() ||
                        priceString.isEmpty() ||
                        qtyString.isEmpty() ||
                        supString.isEmpty() ||
                        phoneString.isEmpty()) {
                    Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
                    return true;
                }

                saveBook();

                finish();
                return true;

            case R.id.action_delete:

                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:

                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(EditorActivity.this);

                            }
                        };

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

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.BOOK_NAME,
                BookContract.BookEntry.BOOK_PRICE,
                BookContract.BookEntry.BOOK_QUANTITY,
                BookContract.BookEntry.SUPPLIER_NAME,
                BookContract.BookEntry.BOOK_PHONE};


        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_QUANTITY);
            int supColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.BOOK_PHONE);

            String name = cursor.getString(nameColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            int qty = cursor.getInt(qtyColumnIndex);
            String supplier = cursor.getString(supColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mQtyEditText.setText(Integer.toString(qty));
            mSupEditText.setText(supplier);
            mPhoneEditText.setText(phone);
            mPhoneUri = Uri.parse(phone);


            Button button = (Button) findViewById(R.id.call_supplier);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mPhoneUri));
                    startActivity(intent);
                }
            });

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQtyEditText.setText("");
        mSupEditText.setText("");
        mPhoneEditText.setText("");
    }


    private void showUnsavedChangesDialog(

            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
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


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
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


    private void deleteBook() {
        if (mCurrentBookUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}