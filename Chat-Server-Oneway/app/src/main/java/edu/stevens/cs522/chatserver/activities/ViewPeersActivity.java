package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private ListView peerList;

    private SimpleCursorAdapter peerAdapter;

    private MessagesDbAdapter dba;

    private Cursor peerCursor;

    private String[] from = new String[] {
            PeerContract.NAME,
            PeerContract.ADDRESS
    };

    private int[] to = new int[] {
        android.R.id.text1,
        android.R.id.text2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        dba = new MessagesDbAdapter(this);
        dba.open();

        peerCursor = dba.fetchAllPeers();
        peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_2, peerCursor, from, to);

        peerList = (ListView) findViewById(R.id.peerList);
        peerList.setAdapter(peerAdapter);

        registerForContextMenu(peerList);
        peerList.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, id);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        peerCursor.close();
        dba.close();
    }
}
