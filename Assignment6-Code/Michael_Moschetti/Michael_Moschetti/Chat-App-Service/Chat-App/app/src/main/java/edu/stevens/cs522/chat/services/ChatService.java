package edu.stevens.cs522.chat.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Process;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Utf8;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.chat.activities.SettingsActivity;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.InetAddressUtils;

import static android.app.Activity.RESULT_OK;


public class ChatService extends Service implements IChatService, SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String TAG = ChatService.class.getCanonicalName();

    protected static final String SEND_TAG = "ChatSendThread";

    protected static final String RECEIVE_TAG = "ChatReceiveThread";

    protected IBinder binder = new ChatBinder();

    protected SendHandler sendHandler;

    protected Thread receiveThread;

    protected HandlerThread sendThread;

    protected DatagramSocket chatSocket;

    protected boolean socketOK = true;

    protected boolean finished = false;

    PeerManager peerManager;

    MessageManager messageManager;

    // TODO added a messenger
    Messenger messenger;

    protected int chatPort;

    @Override
    public void onCreate() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        chatPort = Integer.valueOf(prefs.getString(SettingsActivity.APP_PORT_KEY, Integer.toString(SettingsActivity.DEFAULT_APP_PORT)));
        prefs.registerOnSharedPreferenceChangeListener(this);

        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);

        try {
            chatSocket = new DatagramSocket(chatPort);
        } catch (Exception e) {
            IllegalStateException ex = new IllegalStateException("Unable to init client socket.");
            ex.initCause(e);
            throw ex;
        }

        // TODO initialize the thread that sends messages
        sendThread	= new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        sendThread.start();
        Looper sendLooper = sendThread.getLooper();
        sendHandler	= new SendHandler(sendLooper);
        messenger = new Messenger(sendHandler);
        // end TODO

        /*
         * This is the thread that receives messages.
         */
        receiveThread = new Thread(new ReceiverThread());
        receiveThread.start();
    }

    @Override
    public void onDestroy() {
        finished = true;
        sendHandler.getLooper().getThread().interrupt();  // No-op?
        sendHandler.getLooper().quit();
        receiveThread.interrupt();
        chatSocket.close();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public final class ChatBinder extends Binder {

        public IChatService getService() {
            return ChatService.this;
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(SettingsActivity.APP_PORT_KEY)) {
            try {
                chatSocket.close();
                chatPort = prefs.getInt(SettingsActivity.APP_PORT_KEY, SettingsActivity.DEFAULT_APP_PORT);
                chatSocket = new DatagramSocket(chatPort);
            } catch (IOException e) {
                IllegalStateException ex = new IllegalStateException("Unable to change client socket.");
                ex.initCause(e);
                throw ex;
            }
        }
    }

    @Override
    public void send(InetAddress destAddress, int destPort, String sender, String messageText, ResultReceiver receiver) {
        Message message = sendHandler.obtainMessage();

        // TODO send the message to the sending thread
        Bundle payload = new Bundle();

        payload.putString(SendHandler.DEST_ADDRESS, InetAddressUtils.fromIpAddress(destAddress));
        payload.putInt(SendHandler.DEST_PORT, destPort);
        payload.putString(SendHandler.CHAT_NAME, sender);
        payload.putString(SendHandler.CHAT_MESSAGE, messageText);
        payload.putParcelable(SendHandler.RECEIVER, receiver);

        message.setData(payload);

        sendHandler.sendMessage(message);
    }

    private final class SendHandler extends Handler {

        public static final String CHAT_NAME = "edu.stevens.cs522.chat.services.extra.CHAT_NAME";
        public static final String CHAT_MESSAGE = "edu.stevens.cs522.chat.services.extra.CHAT_MESSAGE";
        public static final String DEST_ADDRESS = "edu.stevens.cs522.chat.services.extra.DEST_ADDRESS";
        public static final String DEST_PORT = "edu.stevens.cs522.chat.services.extra.DEST_PORT";
        public static final String RECEIVER = "edu.stevens.cs522.chat.services.extra.RECEIVER";

        public SendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {

            try {
                InetAddress destAddr = null;

                int destPort = -1;

                byte[] sendData = null;  // Combine sender and message text; default encoding is UTF-8

                ResultReceiver receiver = null;

                // TODO get data from message (including result receiver)
                Bundle data = message.getData();
                destAddr = InetAddressUtils.toIpAddress(data.getString(SendHandler.DEST_ADDRESS).substring(1));
                destPort = data.getInt(SendHandler.DEST_PORT);
                sendData = (data.getString(SendHandler.CHAT_NAME) + ':'
                        + (new Date()).getTime() + ':'
                        + data.getString(SendHandler.CHAT_MESSAGE)).getBytes();
                receiver = data.getParcelable(SendHandler.RECEIVER);
                // End todo

                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, destAddr, destPort);

                chatSocket.send(sendPacket);

                Log.i(TAG, "Sent packet: " + new String(sendData));

                receiver.send(RESULT_OK, null);


            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IO exception: " + e.getMessage());
            }

        }
    }

    private final class ReceiverThread implements Runnable {

        public void run() {

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (!finished && socketOK) {

                try {

                    chatSocket.receive(receivePacket);
                    Log.i(TAG, "Received a packet");

                    InetAddress sourceIPAddress = receivePacket.getAddress();
                    Log.i(TAG, "Source IP Address: " + sourceIPAddress);

                    String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");
                    Log.d("Date", msgContents[1]);
                    final ChatMessage message = new ChatMessage();
                    message.sender = msgContents[0];
                    message.timestamp = new Date(Long.parseLong(msgContents[1]));
                    message.messageText = msgContents[2];

                    Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

                    Peer sender = new Peer();
                    sender.name = message.sender;
                    sender.timestamp = message.timestamp;
                    sender.address = receivePacket.getAddress();
                    sender.port = receivePacket.getPort();

                    peerManager.persistAsync(sender, new IContinue<Long>() {
                        @Override
                        public void kontinue(Long id) {
                            message.senderId = id;
                            messageManager.persistAsync(message);
                        }
                    });

                } catch (Exception e) {

                    Log.e(TAG, "Problems receiving packet.", e);
                    socketOK = false;
                }

            }

        }

    }

}
