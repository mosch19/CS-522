package edu.stevens.cs522.bookstore.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.Set;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;

import static edu.stevens.cs522.bookstore.contracts.BookContract.AUTHORS;
import static edu.stevens.cs522.bookstore.contracts.BookContract.getId;

/**
 * Created by dduggan.
 */

public class BookManager extends Manager<Book> {

    private static final int LOADER_ID = 1;

    private Activity context;

    private static final IEntityCreator<Book> creator = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public BookManager(Activity context) {
        super(context, creator, LOADER_ID);
        this.context = context;
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllBooksAsync(IQueryListener<Book> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery(null, context, BookContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void getBookAsync(long id, final IContinue<Book> callback) {
        // TODO
        String[] projection = { BookContract._ID, BookContract.TITLE, AUTHORS, BookContract.ISBN, BookContract.PRICE };
        String selection = BookContract._ID + "=" + id;
        contentResolver.queryAsync(BookContract.CONTENT_URI, projection, selection, null, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor value) {
                callback.kontinue(new Book(value));
            }
        });
    }

    public void persistAsync(final Book book) {
        // TODO
        ContentValues values = new ContentValues();
        book.writeToProvider(values);
        contentResolver.insertAsync(BookContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    @Override
                    public void kontinue(Uri value) {
                        book.id = BookContract.getId(value);
                        reexecuteQuery(BookContract.CONTENT_URI, null, null, null, (IQueryListener<Book>) context);
                    }
                });
    }

    public void deleteBooksAsync(Set<Long> toBeDeleted) {
        Long[] ids = new Long[toBeDeleted.size()];
        toBeDeleted.toArray(ids);
        String[] args = new String[ids.length];

        StringBuilder sb = new StringBuilder();
        if (ids.length > 0) {
            sb.append(AuthorContract.ID);
            sb.append("=?");
            args[0] = ids[0].toString();
            for (int ix=1; ix<ids.length; ix++) {
                sb.append(" or ");
                sb.append(AuthorContract.ID);
                sb.append("=?");
                args[ix] = ids[ix].toString();
            }
        }
        String select = sb.toString();
        for(String x : args) {
            Log.d("Deleting", x);
        }

        contentResolver.deleteAsync(BookContract.CONTENT_URI(1), select, args,
                new IContinue<Integer>() {
                    @Override
                    public void kontinue(Integer value) {
                        reexecuteQuery(BookContract.CONTENT_URI, null, null, null, (IQueryListener<Book>) context);
                    }
                });
    }

    public void deleteAllBooksAsync() {
        contentResolver.deleteAsync(BookContract.CONTENT_URI, null, null,
            new IContinue<Integer>() {
                @Override
                public void kontinue(Integer value) {
                    reexecuteQuery(BookContract.CONTENT_URI, null, null, null, (IQueryListener<Book>) context);
                }
            });
    }
}