package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.BookManager;
import edu.stevens.cs522.bookstore.managers.TypedCursor;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, IQueryListener {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")

    public static final String CART_SIZE = "cartSize";

	public static final String BOOK_VIEW_KEY = "book_view";

	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    private BookManager bookManager;

    private BookAdapter bookAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)
        if(savedInstanceState != null) {

        }
		// TODO Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);
        // Use a custom cursor adapter to display an empty (null) cursor.
        bookAdapter = new BookAdapter(this, null);
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);
        registerForContextMenu(lv);
        // TODO Set listeners for item selection and multi-choice CAB
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(this);

        // Initialize the book manager and query for all books
        bookManager = new BookManager(this);
        bookManager.getAllBooksAsync(this);
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
                int count = bookAdapter.getCount();
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                checkoutIntent.putExtra(CART_SIZE, count);
                startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                break;

            default:
        }
        return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch(requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if(resultCode == RESULT_OK) {
                    Book result = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    bookManager.persistAsync(result);
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if(resultCode == RESULT_OK) {
                    bookManager.deleteAllBooksAsync();
                }
                break;
        }

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
		
	}

    /*
     * TODO Query listener callbacks
     */

    @Override
    public void handleResults(TypedCursor results) {
        // TODO update the adapter
        bookAdapter.swapCursor(results.getCursor());
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        // TODO update the adapter
        bookAdapter.changeCursor(null);
        bookAdapter.notifyDataSetChanged();
    }


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO query for this book's details, and send to ViewBookActivity
        // ok to do on main thread for BookStoreWithContentProvider
        Intent viewIntent = new Intent(this, ViewBookActivity.class);
        long x = bookAdapter.getItemId(position);
        Book toView = new Book((Cursor) bookAdapter.getItem(position));
        toView.id = position;
        viewIntent.putExtra(BOOK_VIEW_KEY, toView);
        startActivity(viewIntent);
    }


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.books_cab, menu);
        selected = new HashSet<>();
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            selected.add(id);
        } else {
            selected.remove(id);
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete:
                // TODO delete the selected books
                bookManager.deleteBooksAsync(selected);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}