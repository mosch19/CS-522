/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.services.ChatService;
import edu.stevens.cs522.chat.services.IChatService;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ServiceConnection, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

    private EditText messageText;

    private Button sendButton;

    private String[] from = { MessageContract.SENDER, MessageContract.MESSAGE_TEXT };

    private int[] to = { android.R.id.text1, android.R.id.text2 };

    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;

    /*
     * Reference to the service, for sending a message
     */
    private IChatService chatService;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
		if (savedInstanceState == null) {
			PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		}

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.messages);

        // TODO use SimpleCursorAdapter to display the messages received.
        messagesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_2, null, from, to);

        // TODO create the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        messageManager.getAllMessagesAsync(this);
        peerManager = new PeerManager(this);

        // TODO initiate binding to the service
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);

        Intent bindIntent = new Intent(this, ChatService.class);
        bindService(bindIntent, this, Context.BIND_AUTO_CREATE);

        // TODO initialize sendResultReceiver
        sendResultReceiver = new ResultReceiverWrapper(new Handler());
        sendResultReceiver.setReceiver(this);

        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent viewPeers = new Intent(this, ViewPeersActivity.class);
                startActivity(viewPeers);
                break;

            // TODO SETTINGS provide the UI for settings
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (chatService != null) {
            /*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

            InetAddress destAddr;

            int destPort;

            String username;

            String message = null;

            // TODO get destination and message from UI, and username from preferences.
            destinationPort = (EditText) findViewById(R.id.destination_port);
            destPort = Integer.parseInt(destinationPort.getText().toString());

            destinationHost = (EditText) findViewById(R.id.destination_host);
            destAddr = InetAddressUtils.toIpAddress(destinationHost.getText().toString());

            messageText = (EditText) findViewById(R.id.message_text);
            message = messageText.getText().toString();

            // TODO make sure this is returning the proper thing
            username = settings.getString(SettingsActivity.USERNAME_KEY, null);

            // TODO use the service to send a message to the specified destination.
            chatService.send(destAddr, destPort, username, message, sendResultReceiver);

            // End todo

            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        Toast toast;
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                toast = Toast.makeText(this, "Success", Toast.LENGTH_SHORT);
                toast.show();
                break;
            default:
                // TODO show a failure toast message
                toast = Toast.makeText(this, "Failure", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        // TODO
        messagesAdapter.swapCursor(results.getCursor());
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        // TODO
        messagesAdapter.swapCursor(null);
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // TODO initialize chatService
        chatService = ((ChatService.ChatBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        chatService = null;
    }
}