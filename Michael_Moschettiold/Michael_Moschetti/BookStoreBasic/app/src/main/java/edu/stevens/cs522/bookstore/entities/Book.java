package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable{

	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Book> CREATOR
				= new Parcelable.Creator<Book>() {
		public Book createFromParcel(Parcel in) {
			return new Book(in);
		}

		public Book[] newArray(int size) {
			return new Book[size];
		}
	};

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeString(this.title);
		out.writeTypedArray(this.authors, flags);
		// out.writeParcelableArray(this.authors, flags);
		out.writeString(this.isbn);
		out.writeString(this.price);
	}

	public Book(Parcel in){
		this.id = in.readInt();
		this.title = in.readString();
		// don't know if this works but this removed the type intersection error
		this.authors =  in.createTypedArray(Author.CREATOR);
		// this.authors = in.readParcelable(Author.class.getClassLoader());
		this.isbn = in.readString();
		this.price = in.readString();
	}

	public int id;
	
	public String title;
	
	public Author[] authors;
	
	public String isbn;
	
	public String price;

	public Book(int id, String title, Author[] author, String isbn, String price) {
		this.id = id;
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

	public String getFirstAuthor() {
		if (authors != null && authors.length > 0) {
			return authors[0].toString();
		} else {
			return "";
		}
	}

	public String getAuthors() {
		String result = "";
		for(Author x : authors) {
			result += x.toString();
			result += ", ";
		}
		return result;
	}

}