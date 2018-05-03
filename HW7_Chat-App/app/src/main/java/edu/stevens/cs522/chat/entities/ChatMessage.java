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

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    public static final Parcelable.Creator<ChatMessage> CREATOR
            = new Parcelable.Creator<ChatMessage>() {
        public ChatMessage createFromParcel(Parcel in) { return new ChatMessage(in); }

        public ChatMessage[] newArray(int size) { return new ChatMessage[size]; }
    };

    public ChatMessage(String sender, String messageText, String chatRoom, Date timestamp, long senderId) {
        this.sender = sender;
        this.messageText = messageText;
        this.chatRoom = chatRoom;
        this.timestamp = timestamp;
        this.senderId = senderId;
    }

    public int describeContents() { return 0; }

    public ChatMessage(Parcel in) {
        id = in.readLong();
        seqNum = in.readLong();
        messageText = in.readString();
        chatRoom = in.readString();
        timestamp = DateUtils.readDate(in);
        longitude = in.readDouble();
        latitude = in.readDouble();
        sender = in.readString();
        senderId = in.readLong();
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODO
        seqNum = MessageContract.getSequenceNumber(cursor);
        messageText = MessageContract.getMessageText(cursor);
        chatRoom = MessageContract.getChatRoom(cursor);
        timestamp = DateUtils.getDate(cursor, 0);
        longitude = MessageContract.getLongitude(cursor);
        latitude = MessageContract.getLatitude(cursor);
        sender = MessageContract.getSender(cursor);
        senderId = MessageContract.getSenderId(cursor);
    }

    public void writeToProvider(ContentValues out) {
        // TODO
        MessageContract.putSequenceNumber(out, this.seqNum);
        MessageContract.putMessageText(out, this.messageText);
        MessageContract.putChatRoom(out, this.chatRoom);
        DateUtils.putDate(out, TIMESTAMP, this.timestamp);
        MessageContract.putLongitude(out, this.longitude);
        MessageContract.putLatitude(out, this.latitude);
        MessageContract.putSender(out, this.sender);
        MessageContract.putSenderId(out, this.senderId);
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(seqNum);
        out.writeString(messageText);
        out.writeString(chatRoom);
        DateUtils.writeDate(out, timestamp);
        out.writeDouble(longitude);
        out.writeDouble(latitude);
        out.writeString(sender);
        out.writeLong(senderId);
    }

}
