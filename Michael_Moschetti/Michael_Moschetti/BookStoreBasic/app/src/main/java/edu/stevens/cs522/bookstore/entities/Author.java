package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Author implements Parcelable {

	public int describeContents() {
		return 0;
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

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.firstName);
		out.writeString(this.middleInitial);
		out.writeString(this.lastName);
	}

	public Author(Parcel in) {
		this.firstName = in.readString();
		this.middleInitial = in.readString();
		this.lastName = in.readString();
	}

	// NOTE: middleInitial may be NULL!
	
	public String firstName;
	
	public String middleInitial;
	
	public String lastName;

	// this gets called from utils but I wrote it...is it needed?
	public Author() {
	}

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

}
