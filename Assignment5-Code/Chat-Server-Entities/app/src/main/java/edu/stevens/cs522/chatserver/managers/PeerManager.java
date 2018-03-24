package edu.stevens.cs522.chatserver.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.CursorAdapter;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private Activity context;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public PeerManager(Activity context) {
        super(context, creator, LOADER_ID);
        this.context = context;
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery(null, context, PeerContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void getPeerAsync(long id, final IContinue<Peer> callback) {
        // TODO need to check that peer is not null (not in database)
        String selection = PeerContract._ID + "=?";
        String[] selectionArgs = { String.valueOf(id) };
        contentResolver.queryAsync(PeerContract.CONTENT_URI(id), null, selection, selectionArgs, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor value) {
                // if value isn't in table
                if(value.getCount() == 0) {
                    callback.kontinue(null);
                } else {
                    callback.kontinue(new Peer(value));
                }
            }
        });
    }

    public void persistAsync(final Peer peer, final IContinue<Long> callback) {
        // TODO need to ensure the peer is not already in the database
        getPeerAsync(peer.id, new IContinue<Peer>() {
            @Override
            public void kontinue(Peer value) {
                if (value == null){
                    ContentValues values = new ContentValues();
                    peer.writeToProvider(values);
                    contentResolver.insertAsync(PeerContract.CONTENT_URI,
                            values, new IContinue<Uri>() {
                                @Override
                                public void kontinue(Uri value) {
                                    callback.kontinue(PeerContract.getId(value));
                                }
                            });
                }
            }
        });
    }

}
