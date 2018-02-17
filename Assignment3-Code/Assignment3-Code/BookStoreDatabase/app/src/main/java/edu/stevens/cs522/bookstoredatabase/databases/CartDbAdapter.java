package edu.stevens.cs522.bookstoredatabase.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.bookstoredatabase.contracts.AuthorContract;
import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;
import edu.stevens.cs522.bookstoredatabase.entities.Author;
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

    public static class DatabaseHelper extends SQLiteOpenHelper {

        // TODO
        private static final String BOOK_CREATE =
                "create table " + BOOK_TABLE + " ("
                + BookContract._ID + " integer primary key, "
                + BookContract.TITLE + " text not null, "
                + BookContract.AUTHORS + " text not null, "
                + BookContract.ISBN + " text not null, "
                + BookContract.PRICE + " text "
                + ")";

        private static final String AUTHOR_CREATE =
                "create table " + AUTHOR_TABLE + " ("
                + AuthorContract._ID + " integer primary key, "
                + AuthorContract.FK + " integer not null, "
                + AuthorContract.FIRST_NAME + " text not null, "
                + AuthorContract.MIDDLE_INITIAL + " text, "
                + AuthorContract.LAST_NAME + " text not null, "
                + " FOREIGN	KEY	(FK) REFERENCES	books(_id) ON DELETE CASCADE)";

                // + " CREATE INDEX AuthorsBookIndex ON Authors(FK)";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO create both tables
            db.execSQL(BOOK_CREATE);
            db.execSQL(AUTHOR_CREATE);
            db.execSQL("PRAGMA	foreign_keys=1;");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO update both tables...
            //	Log	the	version	upgrade.
            Log.w("TaskDBAdapter",
                    "Upgrading	from version	"	+ oldVersion
                            +	"	to	"	+ newVersion);
            db.execSQL("DROP	TABLE	IF	EXISTS	"	+	BOOK_TABLE);
            db.execSQL("DROP    TABLE   IF  EXISTS  "   +   AUTHOR_TABLE);
            //	Create	a	new	one.
            onCreate(db);
        }
    }


    public CartDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //TODO
    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db,1,1);
    }

    public Cursor fetchAllBooks() {
        // TODO
        String joinQ = "SELECT "
                + BOOK_TABLE + "."
                + BookContract._ID + ", "
                + BookContract.TITLE + ", "
                + BookContract.PRICE + ", "
                + BookContract.ISBN + ", "
                + "GROUP_CONCAT(" + AuthorContract.FIRST_NAME
                + " || ' ' || " + AuthorContract.MIDDLE_INITIAL
                + " || ' ' || " + AuthorContract.LAST_NAME + ", '|') "
                + "as " + BookContract.AUTHORS + " "
                + "FROM " + BOOK_TABLE + " JOIN "
                + AUTHOR_TABLE +" ON "
                + BOOK_TABLE + "." + BookContract._ID + "=" + AUTHOR_TABLE + "." + AuthorContract.FK
                + " GROUP BY " + BOOK_TABLE + "." + BookContract._ID + ", "
                + BookContract.TITLE + ", "
                + BookContract.ISBN + ", "
                + BookContract.PRICE;

        return db.rawQuery(joinQ, null);
//        String[] projection = { BookContract._ID, BookContract.TITLE, BookContract.ISBN, BookContract.PRICE };
//        return db.query(BOOK_TABLE, projection, null, null, null, null, null);
    }

    // TODO link to book constructor for cursors
    public Book fetchBook(long rowId) {
        // TODO
        String fetch = "SELECT * FROM " + BOOK_TABLE;
        String[] projection = { BookContract._ID, BookContract.TITLE, BookContract.AUTHORS, BookContract.ISBN, BookContract.PRICE };
        String selection = BookContract._ID + "=" + Long.toString(rowId);
//        return new Book(db.rawQuery(fetch, null));
        return new Book(db.query(BOOK_TABLE,
                        projection,
                        selection,
                        null, null, null, null));
    }

    // TODO Shouldn't this return a long???
    public long persist(Book book) throws SQLException {
        book.printAuthors();
        ContentValues contentValues = new ContentValues();
        ContentValues authorValues = new ContentValues();
        book.writeToProvider(contentValues);
        book._id = db.insert(BOOK_TABLE, null, contentValues);

        for (int i = 0; i < book.authors.length; i++) {
            book.authors[i].FK = book._id;
            book.authors[i].writeToProvider(authorValues);
            long res= db.insert(AUTHOR_TABLE, null, authorValues);
            authorValues.clear();
        }
        Log.d("In persist", " " + book._id);
        return book._id;
    }

    // TODO need to get a book id
    public boolean delete(Book book) {
        Log.d("Book ID: ", " " + book._id);
        return db.delete(BOOK_TABLE,
                BookContract._ID + "=" + book._id,
                null) > 0;
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
