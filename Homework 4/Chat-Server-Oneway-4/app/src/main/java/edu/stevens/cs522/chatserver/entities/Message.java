package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.util.DateUtils;

import static edu.stevens.cs522.chatserver.contracts.MessageContract.TIMESTAMP;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues
    public int describeContents() { return 0; }

    public static final Parcelable.Creator<Message> CREATOR
            = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) { return new Message(in); }

        public Message[] newArray(int size) { return new Message[size]; }
    };

    public Message(Parcel in) {
        id = in.readLong();
        messageText = in.readString();
        timestamp = DateUtils.readDate(in);
        sender = in.readString();
        senderId = in.readLong();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(messageText);
        DateUtils.writeDate(out, timestamp);
        out.writeString(sender);
        out.writeLong(senderId);
    }

    public Message(Cursor cursor) {
        // TODO
        messageText = MessageContract.getMessageText(cursor);
        timestamp = DateUtils.getDate(cursor, 0);
        sender = MessageContract.getSender(cursor);
        senderId = MessageContract.getSenderId(cursor);
    }

    public void writeToProvider(ContentValues out) {
        MessageContract.putMessageText(out, this.messageText);
        DateUtils.putDate(out, TIMESTAMP, this.timestamp);
        MessageContract.putSender(out, this.sender);
        MessageContract.putSenderId(out, this.senderId);
    }

}
