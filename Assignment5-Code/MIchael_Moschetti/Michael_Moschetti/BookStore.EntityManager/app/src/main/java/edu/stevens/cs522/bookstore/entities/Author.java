package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;

import static android.R.attr.author;

public class Author implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	public long FK;
	
	public String name;

	public Author(String authorText) {
		this.name = authorText;
	}

	public Author(String author, long id) {
		this.FK = id;
		this.name = author;
	}

	public static final Parcelable.Creator<Author> CREATOR
			= new Parcelable.Creator<Author>() {
		public Author createFromParcel(Parcel in) { return new Author(in); }

		public Author[] newArray(int size) { return new Author[size]; }
	};

	private Author(Parcel in) {
		FK = in.readLong();
		name = in.readString();
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(FK);
		out.writeString(name);
	}

	public int describeContents() { return 0; }

	public String toString() { return name; }

	public void writeToProvider(ContentValues out) {
		AuthorContract.putFK(out, this.FK);
		AuthorContract.putName(out, this.name);
	}

}
