package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
        // Used for SYNC
        this.requestManager = new RequestManager(context);
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
        if (!Settings.SYNC) {
            // TODO insert the message into the local database

            Response response = restMethod.perform(request);
            if (response instanceof PostMessageResponse) {
                // TODO update the message in the database with the sequence number

            }
            return response;
        } else {
            /*
             * We will just insert the message into the database, and rely on background
             * sync to upload.
             */
            ChatMessage chatMessage = new ChatMessage();
            // TODO fill the fields with values from the request message

            requestManager.persist(chatMessage);
            return request.getDummyResponse();
        }
    }

    /**
     * For SYNC: perform a sync using a request manager
     * @param request
     * @return
     */
    public Response perform(SynchronizeRequest request) {
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        try {
            /*
             * This is the callback from streaming new local messages to the server.
             */
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        // To avoid losing the first cursor.
                        do {
                            ChatMessage chatMessage = new ChatMessage(messages.getCursor());
                            wr.beginObject();
                            wr.name("chatroom").value(chatMessage.chatRoom);
                            wr.name("text").value(chatMessage.messageText);
                            wr.name("sender").value(chatMessage.sender);
                            wr.name("timestamp").value(chatMessage.timestamp.getTime());
                            wr.name("latitude").value(chatMessage.latitude);
                            wr.name("longitude").value(chatMessage.longitude);
                            wr.name("seqNum").value(chatMessage.seqNum);
                            wr.endObject();
                        } while(messages.moveToNext());
                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            /*
             * Connect to the server and upload messages not yet shared.
             */
            response = restMethod.perform(request, out);

            /*
             * Stream downloaded peer and message information, and update the database.
             * The connection is closed in the finally block below.
             */
            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODO parse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.
            rd.beginObject();
            ArrayList<ChatMessage> chatMessages = new ArrayList<>();
            ArrayList<Peer> peers = new ArrayList<>();
            while(rd.hasNext()) {
                ChatMessage chatMessage = new ChatMessage();
                Peer peer = new Peer();
                String current = rd.nextName();
                // this approach won't work...need sub-loops and other annoyances for this
                switch(current) {
                    case "text":
                        break;
                    case "chatroom":
                        break;
                    case "sender":
                        break;
                    case "timestamp":
                        break;
                    case "latitude":
                        break;
                    case "longitude":
                        break;
                    case "seqNum":
                        break;
                }
                chatMessages.add(chatMessage);
                peers.add(peer);
            }
            /*
             *
             */
            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(0, ErrorResponse.Status.SERVER_ERROR, e.getMessage());

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }


}
