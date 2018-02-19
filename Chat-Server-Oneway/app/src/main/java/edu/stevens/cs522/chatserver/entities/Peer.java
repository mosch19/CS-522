package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.util.DateUtils;
import edu.stevens.cs522.chatserver.util.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class Peer {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public InetAddress address;

    public int port;

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    // TODO Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        DateUtils.writeDate(out, timestamp);
        InetAddressUtils.writeAddress(out, address);
        out.writeInt(port);
    }

    public static final Parcelable.Creator<Peer> CREATOR
            = new Parcelable.Creator<Peer>() {
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    private Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        DateUtils.readDate(in);
        InetAddressUtils.readAddress(in);
        port = in.readInt();
    }

    public Peer() {
    }

    // TODO Cursor
    public Peer(Cursor cursor) {
        name = PeerContract.getName(cursor);
        DateUtils.getDate(cursor, PeerContract.timestampColumn);
        InetAddressUtils.getAddress(cursor, PeerContract.addressColumn);
        port = PeerContract.getPort(cursor);
    }

    // TODO ContentValues
    public void writeToProvider(ContentValues out) {
        PeerContract.putName(out, this.name);
        DateUtils.putDate(out, PeerContract.TIMESTAMP, this.timestamp);
        InetAddressUtils.putAddress(out, PeerContract.ADDRESS, this.address);
        PeerContract.putPort(out, this.port);
    }

}
