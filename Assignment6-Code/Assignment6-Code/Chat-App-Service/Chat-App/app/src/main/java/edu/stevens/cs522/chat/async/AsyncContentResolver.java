package edu.stevens.cs522.chat.async;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import static edu.stevens.cs522.chat.contracts.BaseContract.getId;

/**
 * Created by dduggan.
 */

public class AsyncContentResolver extends AsyncQueryHandler {

    public AsyncContentResolver(ContentResolver cr) {
        super(cr);
    }

    public void insertAsync(Uri uri,
                            ContentValues values,
                            IContinue<Long> callback) {
        this.startInsert(0, callback, uri, values);
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Long> callback = (IContinue<Long>) cookie;
            callback.kontinue(getId(uri));
        }
    }

    public void queryAsync(Uri uri, String[] columns, String select, String[] selectArgs, String order, IContinue<Cursor> callback) {
        // TODO??? Already done?
        this.startQuery(0, callback, uri, columns, select, selectArgs, order);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        // TODO
        Log.d("In Async", "here");
        if(cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
            callback.kontinue(cursor);
        }
    }

    public void deleteAsync(Uri uri, String select, String[] selectArgs, IContinue<Integer> callback) {
        // TODO
        this.startDelete(0, callback, uri, select, selectArgs);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        // TODO
        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Integer> callback = (IContinue<Integer>) cookie;
            callback.kontinue(result);
        }
    }

    public void updateAsync(Uri uri, ContentValues values, String select, String[] selectArgs) {
        // TODO
        this.startUpdate(0, null, uri, values, select, selectArgs);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        // TODO
        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Integer> callback = (IContinue<Integer>) cookie;
            callback.kontinue(result);
        }
    }

}
