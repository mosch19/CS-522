package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;

import static android.R.attr.author;

public class Author implements Parcelable {

	// TODO Modify this to implement the Parcelable interface.

	// NOTE: middleInitial may be NULL!

	public long FK;

	public String firstName;

	public String middleInitial;

	public String lastName;

	public Author(String authorText) {
		String[] name = authorText.split(" ");
		switch (name.length) {
			case 0:
				firstName = lastName = "";
				break;
			case 1:
				firstName = "";
				lastName = name[0];
				break;
			case 2:
				firstName = name[0];
				lastName = name[1];
				break;
			default:
				firstName = name[0];
				middleInitial = name[1];
				lastName = name[2];
		}
	}

	private Author(Parcel in) {
		FK = in.readLong();
		firstName = in.readString();
		middleInitial = in.readString();
		lastName = in.readString();
	}

	public Author() {}

	public Author(Cursor cursor) {
		firstName = AuthorContract.getFirstName(cursor);
		middleInitial = AuthorContract.getMiddleInitial(cursor);
		lastName = AuthorContract.getLastName(cursor);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeLong(FK);
		out.writeString(firstName);
		out.writeString(middleInitial);
		out.writeString(lastName);
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

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (firstName != null && !"".equals(firstName)) {
			sb.append(firstName);
			sb.append(' ');
		}
		if (middleInitial != null && !"".equals(middleInitial)) {
			sb.append(middleInitial);
			sb.append(' ');
		}
		if (lastName != null && !"".equals(lastName)) {
			sb.append(lastName);
		}
		return sb.toString();
	}

	public void writeToProvider(ContentValues out) {
		AuthorContract.putFK(out, this.FK);
		AuthorContract.putFirstName(out, this.firstName);
		AuthorContract.putMiddleInitial(out, this.middleInitial);
		AuthorContract.putLastName(out, this.lastName);
	}

}
