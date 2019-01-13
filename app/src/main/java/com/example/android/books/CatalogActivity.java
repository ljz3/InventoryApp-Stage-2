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

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.books.data.BookContract;
import com.example.android.books.data.BookContract.BookEntry;


public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);

                intent.setData(currentBookUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.BOOK_NAME, "Game of Thrones");
        values.put(BookContract.BookEntry.BOOK_PRICE, 20.0);
        values.put(BookContract.BookEntry.BOOK_QUANTITY, 1);
        values.put(BookContract.BookEntry.SUPPLIER_NAME, "Chapters");
        values.put(BookContract.BookEntry.BOOK_PHONE, "(201) 401-4632");

        Uri uri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

    }


    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sellBook(int id, int quantity) {
        if (quantity > 0) {
            quantity -= 1;
            ContentValues values = new ContentValues();
            values.put(BookEntry.BOOK_QUANTITY, quantity);
            Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
            int rowsAffected = getContentResolver().update(uri, values, null, null);

            Toast.makeText(this, R.string.book_sold , Toast.LENGTH_SHORT).show();
            Log.d("Catalog Activity", "Rows affected :" + rowsAffected + ", with item ID: " + id);

        } else {
            Toast.makeText(this, R.string.no_stock,
                    Toast.LENGTH_SHORT).show();
        }
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
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
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
