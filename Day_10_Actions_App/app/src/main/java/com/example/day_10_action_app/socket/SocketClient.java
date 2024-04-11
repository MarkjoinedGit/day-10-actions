package com.example.day_10_action_app.socket;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class SocketClient extends WebSocketClient {

    private Consumer<String> onMsg;
    private String adsId;

    public SocketClient(URI serverUri, Consumer<String> onMsg, String adsId) {
        super(serverUri);
        this.onMsg = onMsg;
        this.adsId = adsId;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("ws", "connected");
        send(String.format("{\"action\": \"connect\", \"adsId\": \"%s\"}", adsId));
    }

    @Override
    public void onMessage(String message) {
        Log.i("ws", message);
        this.onMsg.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i("ws", "Connection close");
        Thread thread = new Thread(this::reconnect);
        thread.start();
    }

    @Override
    public void onError(Exception ex) {
        Log.e("ws", " " + ex.getMessage());
    }
}
