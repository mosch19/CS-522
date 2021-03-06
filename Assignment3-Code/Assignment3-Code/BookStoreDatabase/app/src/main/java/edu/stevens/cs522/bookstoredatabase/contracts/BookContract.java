package edu.stevens.cs522.bookstoredatabase.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by dduggan.
 */

public class BookContract implements BaseColumns {

    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String AUTHORS = "authors";
    public static final String ISBN = "isbn";
    public static final String PRICE = "price";

    private static int titleColumn = -1;
    private static int authorsColumn = -1;
    private static int isbnColumn = -1;
    private static int priceColumn = -1;

    public static String getTitle(Cursor cursor) {
        if (titleColumn < 0) {
            titleColumn =  cursor.getColumnIndexOrThrow(TITLE);;
        }
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor.getString(titleColumn);
    }
    // TODO find out what the if statements are actually for
    public static String getISBN(Cursor cursor) {
        if (isbnColumn < 0) {
            isbnColumn = cursor.getColumnIndexOrThrow(ISBN);
        }
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor.getString(isbnColumn);
    }

    public static String getPrice(Cursor cursor) {
        if (priceColumn < 0) {
            priceColumn = cursor.getColumnIndexOrThrow(PRICE);
        }
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor.getString(priceColumn);
    }

    public static String[] getAuthors(Cursor cursor) {
        if (authorsColumn < 0) {
            authorsColumn =  cursor.getColumnIndexOrThrow(AUTHORS);;
        }
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return readStringArray(cursor.getString(authorsColumn));
    }

    public static void putTitle(ContentValues values, String title) {
        values.put(TITLE, title);
    }

    public static void putAuthors(ContentValues values, String authors) { values.put(AUTHORS, authors); }

    public static void putISBN(ContentValues values, String isbn) { values.put(ISBN, isbn); }

    public static void putPrice(ContentValues values, String price) { values.put(PRICE, price); }

    /*
     * Synthetic authors column
     */
    public static final char SEPARATOR_CHAR = '|';

    private static final Pattern SEPARATOR =
            Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);

    public static String[] readStringArray(String in) {
        return SEPARATOR.split(in);
    }

    // TODO complete definitions of other getter and setter operations

}
