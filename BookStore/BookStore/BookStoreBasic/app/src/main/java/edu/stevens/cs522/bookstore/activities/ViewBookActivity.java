package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;

public class ViewBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);

		// Pull the book from the intent set in main
		Intent viewIntent = getIntent();
        Book toView = viewIntent.getParcelableExtra(MainActivity.BOOK_VIEW_KEY);

        TextView viewTitle = findViewById(R.id.view_title);
        viewTitle.setText(toView.title);

        TextView viewAuthor = findViewById(R.id.view_author);
        viewAuthor.setText(toView.getAuthors());

        TextView viewISBN = findViewById(R.id.view_isbn);
        viewISBN.setText(toView.isbn);
	}

}