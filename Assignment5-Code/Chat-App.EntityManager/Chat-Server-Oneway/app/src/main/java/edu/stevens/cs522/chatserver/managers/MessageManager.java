package edu.stevens.cs522.chatserver.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Set;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<Message> {

    private static final int LOADER_ID = 1;

    private Activity context;

    private static final IEntityCreator<Message> creator = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public MessageManager(Activity context) {
        super(context, creator, LOADER_ID);
        this.context = context;
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllMessagesAsync(IQueryListener<Message> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery(null, context, MessageContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void persistAsync(final Message message) {
        // TODO
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        contentResolver.insertAsync(MessageContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    @Override
                    public void kontinue(Uri value) {
                        message.id = MessageContract.getId(value);
                        reexecuteQuery(MessageContract.CONTENT_URI, null, null, null, (IQueryListener<Message>) context);
                    }
                });
    }

}
