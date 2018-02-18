package edu.stevens.cs522.chatserver.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class MessagesDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "view_peers";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        // TODO
        private static final String DATABASE_CREATE =
                "create table " + MESSAGE_TABLE + " ("
                + MessageContract._ID + " integer primary key, "
                + MessageContract.MESSAGE_TEXT + " text not null, "
                + MessageContract.TIMESTAMP + " text not null, "
                + MessageContract.SENDER + " text not null, "
                + MessageContract.SENDER_ID + " integer not null "
                + ");"
                +  "create talbe " + PEER_TABLE + " ("
                + PeerContract._ID + " integer primary key, "
                + PeerContract.NAME + " text not null, "
                + PeerContract.TIMESTAMP + " text not null, "
                + PeerContract.ADDRESS + " text not null, "
                + PeerContract.PORT + " integer not null "
                + ");"
                + "CREATE INDEX MessagesPeerIndex ON Messages(peer_fk);"
                + "CREATE INDEX PeerNameIndex ON Peers(name);";

        private static final String MESSAGE_CREATE =
            "create table " + MESSAGE_TABLE + " ("
            + MessageContract._ID + " integer primary key, "
            + MessageContract.MESSAGE_TEXT + " text not null, "
            + MessageContract.TIMESTAMP + " text not null, "
            + MessageContract.SENDER + " text not null, "
            + MessageContract.SENDER_ID + " integer not null "
            + ");";

        private static final String PEER_CREATE =
                "create table " + PEER_TABLE + " ("
                + PeerContract._ID + " integer primary key, "
                + PeerContract.NAME + " text not null, "
                + PeerContract.TIMESTAMP + " text not null, "
                + PeerContract.ADDRESS + " text not null, "
                + PeerContract.PORT + " integer not null "
                + ")";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            onCreate(db);
        }
    }


    public MessagesDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        // TODO
        db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, 1, 1);
    }

    public Cursor fetchAllMessages() {
        // TODO
        return null;
    }

    public Cursor fetchAllPeers() {
        // TODO
        return null;
    }

    public Peer fetchPeer(long peerId) {
        // TODO
        return null;
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TODO
        return null;
    }

    public void persist(Message message) throws SQLException {
        // TODO
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     * @param peer
     * @return The database key of the (inserted or updated) peer record
     * @throws SQLException
     */
    public long persist(Peer peer) throws SQLException {
        // TODO
        throw new SQLException("Failed to add peer "+peer.name);
    }

    public void close() {
        // TODO
        db.close();
    }
}