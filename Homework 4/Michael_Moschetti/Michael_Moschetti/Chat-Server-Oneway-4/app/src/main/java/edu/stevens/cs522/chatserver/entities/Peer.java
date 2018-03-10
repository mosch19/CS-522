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

public class Peer implements Parcelable {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public InetAddress address;

    public int port;

    public Peer() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues
    public static final Parcelable.Creator<Peer> CREATOR
            = new Parcelable.Creator<Peer>() {
        public Peer createFromParcel(Parcel in) { return new Peer(in); }

        public Peer[] newArray(int size) { return new Peer[size]; }
    };

    public Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = DateUtils.readDate(in);
        address = InetAddressUtils.readAddress(in);
        port = in.readInt();
    }

    public Peer(Cursor cursor) {
        // TODO
        id = PeerContract.getId(cursor);
        name = PeerContract.getName(cursor);
        timestamp = PeerContract.getTimestamp(cursor);
        address = PeerContract.getAddress(cursor);
        port = PeerContract.getPort(cursor);
    }

    @Override
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

    public void writeToProvider(ContentValues out) {
        // TODO
        PeerContract.putId(out, this.id);
        PeerContract.putName(out, this.name);
        DateUtils.putDate(out, PeerContract.TIMESTAMP, this.timestamp);
        InetAddressUtils.putAddress(out, PeerContract.ADDRESS, this.address);
        PeerContract.putPort(out, this.port);

    }
}
