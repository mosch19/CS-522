package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.MessageQueue;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.util.DateUtils;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    // TODO Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(messageText);
        DateUtils.writeDate(out, timestamp);
        out.writeString(sender);
        out.writeLong(senderId);
    }

    public static final Parcelable.Creator<Message> CREATOR
            = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    private Message(Parcel in) {
        id = in.readLong();
        messageText = in.readString();
        DateUtils.readDate(in);
        sender = in.readString();
        senderId = in.readLong();
    }

    // TODO need blank public constructor
    public Message() {
    }

    // TODO Cursors
    public Message(Cursor cursor) {
        messageText = MessageContract.getMessageText(cursor);
        DateUtils.getDate(cursor, MessageContract.timestampColumn);
        sender = MessageContract.getSender(cursor);
        senderId = MessageContract.getSenderID(cursor);
    }

    // TODO ContentValues
    public void writeToProvider(ContentValues out) {
        MessageContract.putMessageText(out, this.messageText);
        DateUtils.putDate(out, MessageContract.TIMESTAMP, this.timestamp);
        MessageContract.putSender(out, this.sender);
        MessageContract.putSenderID(out, this.senderId);
    }

}
