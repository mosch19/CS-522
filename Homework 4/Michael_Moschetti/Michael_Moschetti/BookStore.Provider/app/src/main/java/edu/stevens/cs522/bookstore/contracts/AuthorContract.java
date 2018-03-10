package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by dduggan.
 */

public class AuthorContract implements BaseColumns {

    public static final String ID = _ID;

    public static final String FK = "fk";

    public static final String NAME = "name";

    /*
     * NAME column
     */
    private static int fkColumn = -1;

    private static int nameColumn = -1;

    // TODO complete the definitions of the operations for Parcelable, cursors and contentvalues

    public static String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn =  cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues values, String name) {
        values.put(NAME, name);
    }

    public static long getFK(Cursor cursor) {
        if (fkColumn < 0) {
            fkColumn = cursor.getColumnIndexOrThrow(FK);
        }
        return cursor.getLong(fkColumn);
    }

    public static void putFK(ContentValues values, long fk) {
        values.put(FK, fk);
    }

}
