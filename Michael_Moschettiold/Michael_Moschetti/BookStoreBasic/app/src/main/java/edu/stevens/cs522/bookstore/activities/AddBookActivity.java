package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.util.Utils;

public class AddBookActivity extends AppCompatActivity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_book_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
			case R.id.add_book:
				Intent addIntent = new Intent(this, MainActivity.class);
				Book result = addBook();
				addIntent.putExtra(AddBookActivity.BOOK_RESULT_KEY, result);
				setResult(RESULT_OK, addIntent);
				finish();
				break;
			case R.id.cancel_add:
				Intent cancelIntent = new Intent(this, MainActivity.class);
				setResult(RESULT_CANCELED, cancelIntent);
				finish();
				break;
			default:
		}

		return false;
	}

	public Book addBook(){
		// TODO Just build a Book object with the search criteria and return that.
        EditText search_title = findViewById(R.id.search_title);
        EditText search_author = findViewById(R.id.search_author);
        EditText search_isbn = findViewById(R.id.search_isbn);

        String title = search_title.getText().toString();
        Author authors[] = Utils.parseAuthors(search_author.getText().toString());
        String isbn = search_isbn.getText().toString();

        return new Book(1, title, authors, isbn, "0");
	}

}