package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    public Peer() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public Peer(Cursor cursor) {
        // TODO
        id = PeerContract.getId(cursor);
        name = PeerContract.getName(cursor);
        timestamp = PeerContract.getTimestamp(cursor);
        longitude = PeerContract.getLongitude(cursor);
        latitude = PeerContract.getLatitude(cursor);
    }

    public Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = DateUtils.readDate(in);
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Parcelable.Creator<Peer> CREATOR
            = new Parcelable.Creator<Peer>() {
        public Peer createFromParcel(Parcel in) { return new Peer(in); }

        public Peer[] newArray(int size) { return new Peer[size]; }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO
        out.writeLong(id);
        out.writeString(name);
        DateUtils.writeDate(out, timestamp);
        out.writeDouble(longitude);
        out.writeDouble(latitude);
    }

    public void writeToProvider(ContentValues out) {
        PeerContract.putId(out, this.id);
        PeerContract.putName(out, this.name);
        DateUtils.putDate(out, PeerContract.TIMESTAMP, this.timestamp);
        PeerContract.putLongitude(out, this.longitude);
        PeerContract.putLatitude(out, this.latitude);
    }

}
