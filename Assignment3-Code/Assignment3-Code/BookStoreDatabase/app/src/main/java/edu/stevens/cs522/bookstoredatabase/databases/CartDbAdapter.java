package edu.stevens.cs522.bookstoredatabase.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.stevens.cs522.bookstoredatabase.entities.Book;

/**
 * Created by dduggan.
 */

public class CartDbAdapter {

    private static final String DATABASE_NAME = "books.db";

    private static final String BOOK_TABLE = "books";

    private static final String AUTHOR_TABLE = "authors";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;

    // Column declarations for BOOK_TABLE
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String ISBN = "isbn";
    public static final String PRICE = "price";

    // Column indexes for BOOK_TABLE
    public static final int TITLE_KEY = 1;
    public static final int AUTHOR_KEY = TITLE_KEY + 1;
    public static final int ISBN_KEY = AUTHOR_KEY + 1;
    public static final int PRICE_KEY = ISBN_KEY + 1;

    // Column declarations for AUTHOR_TABLE
    public static final String _ID2 = "_id";
    public static final String FIRSTNAME = "firstname";
    public static final String MIDDLEINITIAL = "middleinitial";
    public static final String LASTNAME = "lastname";

    // Column indexes for AUTHOR_TABLE
    public static final int FIRSTNAME_KEY = 1;
    public static final int MIDDLEINITIAL_KEY = FIRSTNAME_KEY + 1;
    public static final int LASTNAME_KEY = MIDDLEINITIAL_KEY + 1;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        // TODO
        private static final String BOOK_CREATE =
                "create table" + BOOK_TABLE + " ("
                + _ID + " integer primary key, "
                + TITLE + " text not null, "
                + AUTHOR + " text not null, "
                + ISBN + " text not null, "
                + PRICE + " text not null "
                + ")";

        private static final String AUTHOR_CREATE =
                "create table" + AUTHOR_TABLE + " ("
                + _ID + " integer primary key, "
                + FIRSTNAME + " text not null,"
                + MIDDLEINITIAL + " text not null,"
                + LASTNAME + " text not null "
                + ")";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO create both tables
            db.execSQL(BOOK_CREATE);
            db.execSQL(AUTHOR_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO update both tables...

            onCreate(db);
        }
    }


    public CartDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public CartDbAdapter open() throws SQLException {
        // TODO
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public Cursor fetchAllBooks() {
        // TODO
        String[] projection = { _ID, TITLE, AUTHOR, ISBN, PRICE };
        return db.query(BOOK_TABLE,
                        projection,
                        null, null, null, null, null);
        // return null;
    }

    // TODO link to book constructor for cursors
    public Book fetchBook(long rowId) {
        // TODO
        String[] projection = { _ID, TITLE, AUTHOR, ISBN, PRICE };
        String selection = _ID + "=" + Long.toString(rowId);
        return new Book(db.query(BOOK_TABLE,
                        projection,
                        selection,
                        null, null, null, null));

        // return null;
    }

    public void persist(Book book) throws SQLException {
        // TODO how to deal with Author[] also should I insert the new author here as well?
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, book.title);
        contentValues.put(AUTHOR, book.authors);
        contentValues.put(ISBN, book.isbn);
        contentValues.put(PRICE, book.price);
        db.insert(BOOK_TABLE, null, contentValues);
    }

    // TODO need to get a book id somehow...
    public boolean delete(Book book) {
        db.delete(BOOK_TABLE,
                _ID + "=" + book.id,
                null);
        return false;
    }

    public boolean deleteAll() {
        // TODO return true for some reason? Is it necessary to drop all tables or just delete all records?
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + AUTHOR_TABLE);
        return false;
    }

    public void close() {
        // TODO close...
        db.close();
    }

}
