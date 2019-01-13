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

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.books.data.BookContract.BookEntry;


public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView qtyTextView = (TextView) view.findViewById(R.id.qty);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView supplierTextView = (TextView) view.findViewById(R.id.supplier);
        TextView phoneTextView = (TextView) view.findViewById(R.id.phone);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_NAME);
        int qtyColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_PRICE);
        int supplierColumnIndex = cursor.getColumnIndex(BookEntry.SUPPLIER_NAME);
        int phoneColumnIndex = cursor.getColumnIndex(BookEntry.BOOK_PHONE);

        final int bookID = cursor.getInt(idColumnIndex);
        String bookName = cursor.getString(nameColumnIndex);
        final String bookQty = cursor.getString(qtyColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookSupplier = cursor.getString(supplierColumnIndex);
        String bookPhone = cursor.getString(phoneColumnIndex);
        final int bookQuantity = cursor.getInt(qtyColumnIndex);



        nameTextView.setText("Name: " + bookName);
        qtyTextView.setText("Quantity: " + bookQty);
        priceTextView.setText(" Price: " + bookPrice);
        supplierTextView.setText("Supplier Name: " + bookSupplier);
        phoneTextView.setText("Supplier Phone: " + bookPhone);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogActivity activity = (CatalogActivity) context;
                activity.sellBook(bookID, bookQuantity);
            }
        });

    }
}
