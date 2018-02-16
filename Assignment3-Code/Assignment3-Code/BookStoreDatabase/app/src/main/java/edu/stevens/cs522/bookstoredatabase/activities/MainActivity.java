package edu.stevens.cs522.bookstoredatabase.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;
import edu.stevens.cs522.bookstoredatabase.databases.CartDbAdapter;
import edu.stevens.cs522.bookstoredatabase.entities.Book;

public class MainActivity extends Activity {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;

    public static final String BOOK_VIEW_KEY = "book_view";
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

	static final String CART_CONTENTS = "cart";
	static final String CART_SIZE = "cart_size";

	// The database adapter
	private CartDbAdapter dba;
	// Todo don't use the arrayList anymore
	private ArrayList<Book> shoppingCart;
	private SimpleCursorAdapter simpleCursorAdapter;
	private ListView listView;
    // TODO add options for the simpleCursorAdapter
	private String[] from = new String[] {
            BookContract.TITLE,
            BookContract.AUTHORS
    };
	private int[] to = new int[] {
	        android.R.id.text1,
            android.R.id.text2
    };


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)
		if(savedInstanceState != null) {
		    // TODO restore the database
			shoppingCart = savedInstanceState.getParcelableArrayList(CART_CONTENTS);
		}
		// TODO Set the layout (use cart.xml layout)
		setContentView(R.layout.cart);
		// TODO open the database using the database adapter
		dba = new CartDbAdapter(this);
		dba.open();
        // TODO query the database using the database adapter, and manage the cursor on the main thread
        dba.fetchAllBooks();
        // TODO use SimpleCursorAdapter to display the cart contents.
        simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, dba.fetchAllBooks(), from, to);
        // startManagingCursor(dba.fetchAllBooks());
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(simpleCursorAdapter);
        registerForContextMenu(listView);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO inflate a menu with ADD and CHECKOUT options
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookstore_menu, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO ADD provide the UI for adding a book
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            // TODO CHECKOUT provide the UI for checking out
            case R.id.checkout:
            	Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
            	startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                break;

            default:
        }
        return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch(requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                if(resultCode == RESULT_OK) {
                    Book result = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    dba.persist(result);
                    simpleCursorAdapter.changeCursor(dba.fetchAllBooks());
                    simpleCursorAdapter.notifyDataSetChanged();
                    break;
                } else if(resultCode == RESULT_CANCELED) {
                    break;
                }

            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                if(resultCode == RESULT_OK) {
                    dba.deleteAll();
                } else if(resultCode == RESULT_CANCELED) {
                    break;
                }
                break;
        }


	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.view_book:
                Intent viewIntent = new Intent(this, ViewBookActivity.class);
                Book toView = dba.fetchBook(info.id);
                viewIntent.putExtra(BOOK_VIEW_KEY, toView);
                startActivity(viewIntent);
                return true;
            case R.id.delete_book:
                Book toDelete = dba.fetchBook(info.id);
                dba.delete(toDelete);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    @Override
    public void onDestroy() {
	    dba.close();
	    dba.deleteAll();
        super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
        savedInstanceState.putParcelableArrayList(CART_CONTENTS, shoppingCart);
        super.onSaveInstanceState(savedInstanceState);
	}
	
}