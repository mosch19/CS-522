package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();

	public static final String CART_SIZE = "cartSize";

	public static final String BOOK_VIEW_KEY = "book_view";
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    static final private int LOADER_ID = 1;

    BookAdapter bookAdapter;

    ContentResolver cr;

    LoaderManager lm;

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
        cr = getContentResolver();

        lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);
        registerForContextMenu(lv);

        // TODO set listeners for item selection and multi-choice CAB
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(this);

        // TODO use loader manager to initiate a query of the database
        getLoaderManager().initLoader(0, null, this);
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
                    ContentValues resultVal = new ContentValues();
                    result.writeToProvider(resultVal);
                    Uri baseuri = BookContract.CONTENT_URI;
                    cr.insert(baseuri, resultVal);
                    this.getLoaderManager().restartLoader(LOADER_ID,null,this);
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if(resultCode == RESULT_OK) {
                    cr.delete(BookContract.CONTENT_URI, null, null);
                    getLoaderManager().restartLoader(LOADER_ID, null, this);
                }
                break;
        }

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
		
	}

    /*
     * Loader callbacks
     */

	@Override
	public Loader onCreateLoader(int id, Bundle args) {
		// TODO use a CursorLoader to initiate a query on the database
        String selection = "here";
        String[] projection = {};
        return	new	CursorLoader(this,
                BookContract.CONTENT_URI,
                projection,	null,	null,	null);
	}

	@Override
	public void onLoadFinished(Loader loader, Object data) {
        // TODO populate the UI with the result of querying the provider
        Log.i("Loader finished", "Load finished");
        this.bookAdapter.swapCursor((Cursor) data);
	}

	@Override
	public void onLoaderReset(Loader loader) {
        // TODO reset the UI when the cursor is empty
        this.bookAdapter.swapCursor(null);
	}


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO query for this book's details, and send to ViewBookActivity
        // ok to do on main thread for BookStoreWithContentProvider
        Intent viewIntent = new Intent(MainActivity.this, ViewBookActivity.class);
        long x = bookAdapter.getItemId(position);
        Log.d("IDS in item click", x + " " + id);
        String[] projection = {};
        String selection = "";
        String[] selectionArgs = {};
        // May need to add 1 since 1 indexed
        Book toView = new Book((Cursor) bookAdapter.getItem(position));
        toView._id = position;
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
        selected = new HashSet<Long>();
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
            case R.id.delete_book:
                // TODO delete the selected books
                for (long x : selected) {
                    cr.delete(BookContract.CONTENT_URI(x), x + "", null);
                }
                getLoaderManager().restartLoader(LOADER_ID, null, this);
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
