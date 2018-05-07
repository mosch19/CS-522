package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.net.Uri;
import android.os.ResultReceiver;

import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(final RegisterRequest request) {
        Response response = restMethod.perform(request);
        if (response instanceof RegisterResponse) {
            // TODO update the sender senderId in settings, updated peer record PK
            Settings.saveSenderId(context, ((RegisterResponse) response).getSenderId());
            PeerManager peerManager = new PeerManager(context);
            Peer sender = new Peer();
            sender.name = request.chatname;
            sender.timestamp = request.timestamp;
            sender.longitude = request.longitude;
            sender.latitude = request.latitude;

            peerManager.persistAsync(sender, new IContinue<Long>() {
                @Override
                public void kontinue(Long id) {
                    request.senderId = id;
                }
            });
        }
        return response;
    }

    public Response perform(PostMessageRequest request) {
        // TODO insert the message into the local database
        Response response = restMethod.perform(request);
        if (response instanceof PostMessageResponse) {
            // TODO update the message in the database with the sequence number
            long seqNum = ((PostMessageResponse) response).getMessageId();
            MessageManager messageManager = new MessageManager(context);
            messageManager.persistAsync(new ChatMessage(Settings.getChatName(context), request.message, request.chatRoom, DateUtils.now(), Settings.getSenderId(context), request.longitude, request.latitude, seqNum));
        }
        return response;
    }

}
