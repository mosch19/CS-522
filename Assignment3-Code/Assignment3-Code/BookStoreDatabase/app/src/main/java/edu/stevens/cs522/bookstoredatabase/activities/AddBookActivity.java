package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.entities.Author;
import edu.stevens.cs522.bookstoredatabase.entities.Book;


public class AddBookActivity extends Activity {
	
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
		// TODO provide ADD and CANCEL options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addbook_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
        switch(item.getItemId()) {
            // ADD: return the book details to the BookStore activity
            case R.id.add_book:
                Intent addIntent = new Intent(this, MainActivity.class);
                Book result = addBook();
                addIntent.putExtra(AddBookActivity.BOOK_RESULT_KEY, result);
                setResult(RESULT_OK, addIntent);
                finish();
                break;
            // CANCEL: cancel the request
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
		EditText search_title = (EditText) findViewById(R.id.search_title);
		EditText search_author = (EditText) findViewById(R.id.search_author);
		EditText search_isbn = (EditText) findViewById(R.id.search_isbn);

		String title = search_title.getText().toString();
		Author authors[] = Utils.parseAuthors(search_author.getText().toString());
		String isbn = search_isbn.getText().toString();

		return new Book(1, title, authors, isbn, "0");
	}

}