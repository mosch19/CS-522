package edu.stevens.cs522.bookstoredatabase.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;

import static edu.stevens.cs522.bookstoredatabase.contracts.BookContract.getAuthors;

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
		isbn = in.readString();
		price = in.readString();
        authors = in.createTypedArray(Author.CREATOR);
	}

	public void writeToParcel(Parcel out, int flags) {
		// TODO save state to parcel
		out.writeLong(id);
		out.writeString(title);
		out.writeString(isbn);
		out.writeString(price);
        out.writeTypedArray(authors, flags);
	}

	// TODO find out why it breaks inside this constructor. Also why only the second author appears in the listView
	public Book(Cursor cursor) {
	    // this for some reason causes the crash prematurely
        if (cursor.isNull(0)) {
            // So the cursor is not null... why are the get methods failing...
            Log.d("The cursor is null ", " haha what");
        }
        Log.d("Cursor info: ", " " + cursor.getColumnCount());
	    Log.d("Inside cursor ", "Hallo");
		// TODO init from cursor
		title = BookContract.getTitle(cursor);
		//TODO call the authors constructor?
		String[] in = BookContract.getAuthors(cursor);

		authors = new Author[in.length];
		for (int i = 0; i < in.length; i++) {
			authors[i] = new Author(in[i]);
		}
		isbn = BookContract.getISBN(cursor);
		price = BookContract.getPrice(cursor);
	}

	// TODO should I use a standard constructor for book? Or just insert it to the db?
	public Book(int id, String title, Author[] author, String isbn, String price) {
		this.id = id;
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

	public void writeToProvider(ContentValues out) {
		// TODO write to ContentValues
        BookContract.putTitle(out, this.title);
        BookContract.putISBN(out, this.isbn);
        BookContract.putPrice(out, this.price);
	}

    public String toString() {
	    return this.title + " " + this.isbn;
    }

    public void printAuthors() {
		for (int i = 0; i < authors.length; i++) {
		    Log.d("Author is : ", " " + authors[i].toString());
		}
	}

}