package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    // TODO add loader callbacks

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */
    private ListView peerList;

    private SimpleCursorAdapter peerAdapter;

    private String[] from = {PeerContract.NAME, PeerContract.ADDRESS};

    private int[] to = {android.R.id.text1, android.R.id.text2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with empty cursor (null)
        peerList = (ListView) findViewById(R.id.peerList);

        Cursor peers = getContentResolver().query(PeerContract.CONTENT_URI, null, null, null, null);
        peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_2, peers, from, to);

        peerList.setAdapter(peerAdapter);
        peerList.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

}
