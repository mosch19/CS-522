package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstore.R;

import static edu.stevens.cs522.bookstore.activities.MainActivity.CART_SIZE;


public class CheckoutActivity extends Activity {

	private int cartSize;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);

		Intent cartIntent = getIntent();
		cartSize = cartIntent.getIntExtra(CART_SIZE, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO display ORDER and CANCEL options.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		Intent resultIntent = new Intent(this, MainActivity.class);
		switch(item.getItemId()) {
			case R.id.order:
				// ORDER: display a toast message of how many books have been ordered and return
				Toast checkoutOk = Toast.makeText(this, "Ordered " + Integer.toString(cartSize) + " books.", Toast.LENGTH_SHORT);
				checkoutOk.show();

				setResult(RESULT_OK, resultIntent);
				finish();
				break;
			case R.id.cancel_checkout:
				// CANCEL: just return with REQUEST_CANCELED as the result code
				setResult(RESULT_CANCELED, resultIntent);
				finish();
				break;
		}

		return false;
	}
	
}