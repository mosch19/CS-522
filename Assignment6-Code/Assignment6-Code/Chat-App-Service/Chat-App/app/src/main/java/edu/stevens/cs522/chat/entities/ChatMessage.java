package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.util.DateUtils;

import static edu.stevens.cs522.chat.contracts.MessageContract.TIMESTAMP;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public ChatMessage() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public int describeContents() { return 0; }

    public static final Parcelable.Creator<ChatMessage> CREATOR
            = new Parcelable.Creator<ChatMessage>() {
        public ChatMessage createFromParcel(Parcel in) { return new ChatMessage(in); }

        public ChatMessage[] newArray(int size) { return new ChatMessage[size]; }
    };

    public ChatMessage(Parcel in) {
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

    public ChatMessage(Cursor cursor) {
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
