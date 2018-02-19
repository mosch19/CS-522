package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer_id";

    private MessagesDbAdapter dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        dba = new MessagesDbAdapter(this);
        dba.open();

        Intent viewIntent = getIntent();
        long id = viewIntent.getParcelableExtra(ViewPeerActivity.PEER_ID_KEY);
        Peer peer = dba.fetchPeer(id);

        TextView user_name = (TextView) findViewById(R.id.view_user_name);
        user_name.setText(peer.name);

        TextView timestamp = (TextView) findViewById(R.id.view_timestamp);
        timestamp.setText(peer.timestamp.toString());

        TextView address = (TextView) findViewById(R.id.view_address);
        address.setText(peer.address.toString());

        TextView port = (TextView) findViewById(R.id.view_port);
        port.setText(peer.port);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dba.close();
    }

}
