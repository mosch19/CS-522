package edu.stevens.cs522.chat.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Message;
import android.util.Log;

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    private static final String MESSAGES_CREATE =
            "CREATE TABLE " + MESSAGES_TABLE + " ("
                    + MessageContract._ID + " INTEGER PRIMARY KEY, "
                    + MessageContract.SEQUENCE_NUMBER + " INTEGER NOT NULL, "
                    + MessageContract.MESSAGE_TEXT + " TEXT NOT NULL, "
                    + MessageContract.CHAT_ROOM + " TEXT NOT NULL, "
                    + MessageContract.TIMESTAMP + " REAL NOT NULL, "
                    + MessageContract.LONGITUDE + " REAL NOT NULL, "
                    + MessageContract.LATITUDE + " INTEGER NOT NULL, "
                    + MessageContract.SENDER + " TEXT NOT NULL, "
                    + MessageContract.SENDER_ID + " INTEGER NOT NULL"
                    + ")";

    private static final String PEERS_CREATE =
            "CREATE TABLE " + PEERS_TABLE + " ("
                    + PeerContract._ID + " INTEGER PRIMARY KEY, "
                    + PeerContract.NAME + " TEXT NOT NULL, "
                    + PeerContract.TIMESTAMP + " INTEGER NOT NULL, "
                    + PeerContract.LONGITUDE + " REAL NOT NULL, "
                    + PeerContract.LATITUDE + " REAL NOT NULL"
                    +")";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO initialize database tables
            db.execSQL(MESSAGES_CREATE);
            db.execSQL(PEERS_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO upgrade database if necessary
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, 0, 1);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return MESSAGE_CONTENT_PATH;
            case PEERS_ALL_ROWS:
                return PEER_CONTENT_PATH;
            case MESSAGES_SINGLE_ROW:
                return MESSAGE_CONTENT_PATH_ITEM;
            case PEERS_SINGLE_ROW:
                return PEER_CONTENT_PATH_ITEM;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("Inserting", "x");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row;
        Uri instanceUri;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new message.
                // Make sure to notify any observers
                row = db.insert(MESSAGES_TABLE, null, values);
                if (row != 0){
                    instanceUri = MessageContract.CONTENT_URI(row);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);
                    return instanceUri;
                } else {
                    throw new IllegalStateException("insert: bad case");
                }
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new peer.
                // Make sure to notify any observers
                String[] selectionArgs = { values.getAsString(PeerContract.NAME) };
                String[] projection = {PeerContract.ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.LONGITUDE, PeerContract.LATITUDE };
                instanceUri = PeerContract.CONTENT_URI("1");
                Cursor peerCursor = query(instanceUri, projection, PeerContract.NAME+"=?", selectionArgs, null);
                // Check to see if peer is already in the database
                if(!peerCursor.moveToFirst()) {
                    Log.d("New peer", "here");
                    values.remove(PeerContract.ID);
                    row = db.insert(PEERS_TABLE, null, values);
                    if (row != 0) {
                        instanceUri = PeerContract.CONTENT_URI(row);
                        ContentResolver cr = getContext().getContentResolver();
                        cr.notifyChange(instanceUri, null);
                        return instanceUri;
                    } else {
                        throw new IllegalStateException("insert: bad case");
                    }
                } else {
                    Log.d("Updating", "here");
                    // update the record
                    String select = PeerContract.ID + "=" + PeerContract.getId(peerCursor);
                    row = update(PeerContract.CONTENT_URI(PeerContract.getId(peerCursor)), values, select, null);
                    if (row != 0) {
                        instanceUri = MessageContract.CONTENT_URI(row);
                        ContentResolver cr = getContext().getContentResolver();
                        cr.notifyChange(instanceUri, null);
                        return instanceUri;
                    } else {
                        throw new IllegalStateException("insert: bad case");
                    }

                }

            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection_messages = { MessageContract.ID, MessageContract.MESSAGE_TEXT, MessageContract.CHAT_ROOM, MessageContract.TIMESTAMP, MessageContract.LONGITUDE, MessageContract.LATITUDE, MessageContract.SENDER, MessageContract.SENDER_ID };
        String[] projection_peers = { PeerContract.ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.LONGITUDE, PeerContract.LATITUDE };

        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                cursor = db.query(MESSAGES_TABLE, projection_messages, null, null, null, null, sortOrder);
                break;
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                cursor = db.query(PEERS_TABLE, projection_peers, null, null, null, null, sortOrder);
                break;
            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                cursor = db.query(MESSAGES_TABLE, projection_messages, selection, selectionArgs, null, null, sortOrder);
                break;
            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                cursor = db.query(PEERS_TABLE, projection_peers, selection, selectionArgs, null, null, sortOrder);
                Log.d("Cursor info in query", cursor.getCount() + "");
                break;
            default:
                throw new IllegalStateException("insert: bad case");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Implement this to handle requests to update one or more rows.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case PEERS_SINGLE_ROW:
                return db.update(PEERS_TABLE, values, selection, selectionArgs);
            case MESSAGES_ALL_ROWS:
            case PEERS_ALL_ROWS:
            case MESSAGES_SINGLE_ROW:
            default:
                throw new IllegalStateException("update: bad case");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
            case PEERS_ALL_ROWS:
            case MESSAGES_SINGLE_ROW:
            case PEERS_SINGLE_ROW:
            default:
                throw new IllegalStateException("delete: bad case");
        }
    }

}
