package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.entities.Book;


public class ViewBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);

		// TODO get book as parcelable intent extra and populate the UI with book details.
		Intent viewIntent = getIntent();
		Book book = viewIntent.getParcelableExtra(MainActivity.BOOK_VIEW_KEY);
		Log.i("Fetched book: ", " " + book.toString());
		TextView viewTitle = (TextView) findViewById(R.id.view_title);
		viewTitle.setText(book.title);
		TextView viewAuthor = (TextView) findViewById(R.id.view_author);
		viewAuthor.setText(book.getFirstAuthor());

		TextView viewISBN = (TextView) findViewById(R.id.view_isbn);
		viewISBN.setText(book.isbn);
	}

}