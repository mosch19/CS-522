package edu.stevens.cs522.bookstoredatabase.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import edu.stevens.cs522.bookstoredatabase.entities.Author;

import static android.R.attr.author;

/**
 * Created by dduggan.
 */

public class AuthorContract implements BaseColumns {

    public static final String _ID = "_id";
    public static final String FK = "fk";
    public static final String FIRST_NAME = "first";
    public static final String MIDDLE_INITIAL = "initial";
    public static final String LAST_NAME = "last";

    private static int fkColumn = -1;
    private static int firstNameColumn = -1;
    private static int middleInitialColumn = -1;
    private static int lastNameColumn = -1;

    public static String getFirstName(Cursor cursor) {
        if (firstNameColumn < 0) {
            firstNameColumn =  cursor.getColumnIndexOrThrow(FIRST_NAME);;
        }
        return cursor.getString(firstNameColumn);
    }

    public static String getMiddleInitial(Cursor cursor) {
        if(middleInitialColumn < 0) {
            middleInitialColumn = cursor.getColumnIndexOrThrow(MIDDLE_INITIAL);
        }
        return cursor.getString(middleInitialColumn);
    }

    public static String getLastName(Cursor cursor) {
        if(lastNameColumn < 0) {
            lastNameColumn = cursor.getColumnIndexOrThrow(LAST_NAME);
        }
        return cursor.getString(lastNameColumn);
    }

    public static void putFirstName(ContentValues values, String firstName) {
        values.put(FIRST_NAME, firstName);
    }

    public static void putMiddleInitial(ContentValues values, String middleInitial) {
        values.put(MIDDLE_INITIAL, middleInitial);
    }

    public static void putLastName(ContentValues values, String lastName) {
        values.put(LAST_NAME, lastName);
    }

    public static long getFk(Cursor cursor) {
        return cursor.getLong(fkColumn);
    }

    public static void putFK(ContentValues values, long fk) {
        values.put(FK, fk);
    }
}
