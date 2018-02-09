package edu.stevens.cs522.bookstoredatabase.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;

public class Book implements Parcelable {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	
	public String title;
	
	public Author[] authors;
	
	public String isbn;
	
	public String price;

    public Book() {
    }

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
		out.writeParcelableArray(authors, 0);
		out.writeString(isbn);
		out.writeString(price);
	}

	public Book(Cursor cursor) {
		// TODO init from cursor
		title = BookContract.getTitle(cursor);
		//TODO call the authors constructor?
		authors = BookContract.getAuthors(cursor);
		isbn = BookContract.getISBN(cursor);
		price = BookContract.getPrice(cursor);
	}

	public void writeToProvider(ContentValues out) {
		// TODO write to ContentValues
        BookContract.putTitle(out);
        BookContract.putAuthors(out);
        BookContract.putISBN(out);
        BookContract.putPrice(out);
	}


}