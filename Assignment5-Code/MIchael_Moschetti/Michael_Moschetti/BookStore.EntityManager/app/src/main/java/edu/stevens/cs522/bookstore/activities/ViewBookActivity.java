package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.R;


public class ViewBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";

	private ArrayAdapter<String> authorsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);

		// TODO get book as parcelable intent extra and populate the UI with book details.
		Intent viewIntent = getIntent();
		Book book = viewIntent.getParcelableExtra(MainActivity.BOOK_VIEW_KEY);

		authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, book.getAuthorList());

		TextView viewTitle = (TextView) findViewById(R.id.view_title);
		viewTitle.setText(book.title);

		ListView viewAuthors = (ListView) findViewById(R.id.view_authors);
		viewAuthors.setAdapter(authorsAdapter);

		TextView viewISBN = (TextView) findViewById(R.id.view_isbn);
		viewISBN.setText(book.isbn);


	}

}