package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

import static android.R.attr.author;

public class Author implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	
	public String name;

	public Author(String authorText) {
		this.name = authorText;
	}

	public int describeContents() { return 0; }

	public void writeToParcel(Parcel out, int flags) {
	    out.writeString(name);
    }

    public static final Parcelable.Creator<Author> CREATOR
            = new Parcelable.Creator<Author>() {
	    public Author createFromParcel(Parcel in) {
	        return new Author(in);
        }

        public Author[] newArray(int size) {
	        return new Author[size];
        }
    };

    private Author(Parcel in) {
        name = in.readString();
    }

}
