package edu.stevens.cs522.bookstore.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.util.BooksAdapter;

import static edu.stevens.cs522.bookstore.activities.ViewBookActivity.BOOK_KEY;

public class MainActivity extends AppCompatActivity {
	
	// Use this when logging errors and warnings.
	private static final String TAG = MainActivity.class.getCanonicalName();

    public static final String BOOK_VIEW_KEY = "book_view";
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

	// There is a reason this must be an ArrayList instead of a List.
    static final String CART_CONTENTS = "cart";
    static final String CART_SIZE = "cart_size";

	private ArrayList<Book> shoppingCart;

	// Public so all methods can call NotifyDataSetChanged()
    private BooksAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		shoppingCart = new ArrayList<>();
		adapter = new BooksAdapter(this, shoppingCart);

		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)
		if (savedInstanceState != null) {
			// restore shopping cart
            shoppingCart = savedInstanceState.getParcelableArrayList(CART_CONTENTS);
		}

		setContentView(R.layout.cart);

        ListView listView = findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        // set up the list view to handle the context menu
        registerForContextMenu(listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookstore_menu, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            case R.id.checkout:
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                checkoutIntent.putExtra(CART_SIZE, shoppingCart.size());
                startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                break;

            default:
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
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
                Book toView = shoppingCart.get((int) info.id);
                viewIntent.putExtra(BOOK_VIEW_KEY, toView);
                startActivity(viewIntent);
                return true;
            case R.id.delete_book:
                shoppingCart.remove((int) info.id);
                adapter.notifyDataSetChanged();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
                if (resultCode == RESULT_OK) {
                    Book result	= intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    shoppingCart.add(result);
                    adapter.notifyDataSetChanged();
                    break;
                } else if (resultCode == RESULT_CANCELED) {
                    break;
                } // TODO CHECK OUT REQUEST DOES NOT ACTUALLY WORK WHAT IS GOING ON
            case CHECKOUT_REQUEST:
                if (resultCode == RESULT_OK) {
                    // CHECKOUT: empty the shopping cart.
                    shoppingCart.clear();
                    adapter.notifyDataSetChanged();
                } else if (resultCode == RESULT_CANCELED) {
                    break;
                }

                break;
        }

        // get rid of that shopping cart message
        TextView emptyCart = findViewById(android.R.id.empty);
        if (shoppingCart.isEmpty()) {
            emptyCart.setVisibility(View.VISIBLE);
        } else {
            emptyCart.setVisibility(View.INVISIBLE);
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelableArrayList(CART_CONTENTS, shoppingCart);

		// the IDE kept complaining about this not being here
		super.onSaveInstanceState(savedInstanceState);
	}
	
}
