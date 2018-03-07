package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class Book implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	
	public String title;
	
	public Author[] authors;
	
	public String isbn;
	
	public String price;

	public int describeContents() { return 0; }

	public static final Parcelable.Creator<Book> CREATOR
			= new Parcelable.Creator<Book>() {
		public Book createFromParcel(Parcel in) { return new Book(in); }

		public Book[] newArray(int size) { return new Book[size]; }
	};

    public Book(String title, Author[] authors, String isbn, String price) {
    	this.title = title;
    	this.authors = authors;
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

	public Book(Parcel in) {
		// TODO init from parcel
		id = in.readLong();
		title = in.readString();
		authors = in.createTypedArray(Author.CREATOR);
		isbn = in.readString();
		price = in.readString();
	}

	public void writeToParcel(Parcel out, int flags) {
		// TODO save state to parcel
		out.writeLong(id);
		out.writeString(title);
		out.writeTypedArray(authors, flags);
		out.writeString(isbn);
		out.writeString(price);
	}

	public Book(Cursor cursor) {
		// TODO init from cursor
		title = BookContract.getTitle(cursor);
		String[] in = BookContract.getAuthors(cursor);
		authors = new Author[in.length];
		for(int i = 0; i < in.length; i++) {
			authors[i] = new Author(in[i]);
		}
		isbn = BookContract.getISBN(cursor);
		price = BookContract.getPrice(cursor);
	}

	public void writeToProvider(ContentValues out) {
		// TODO write to ContentValues
		BookContract.putTitle(out, this.title);
		BookContract.putAuthors(out, getAuthors());
		BookContract.putISBN(out, this.isbn);
		BookContract.putPrice(out, this.price);
	}

	public String getAuthors() {
		String result = " ";
		for (int i = 0; i < authors.length; i++) {
			result += authors[i] + ",";
		}
		// Cut off last comma
		result = result.substring(0, result.length() - 1);
		return result;
	}

	public String[] getAuthorList() {
		String[] result = new String[authors.length];
		for (int i = 0; i < authors.length; i++) {
			result[i] = authors[i].toString();
		}
		return result;
	}

	public String toString() {
		return this.title + " by " + this.getFirstAuthor() + " ref by " + this.isbn + " costs " + this.price;
	}
}