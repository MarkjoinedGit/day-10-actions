package com.example.day_10_action_app.socket;

import java.net.URI;
import java.util.function.Consumer;

public class OrderSocket {

    private SocketClient wsClient;
    private URI uri;
    private String adsId;

    public OrderSocket(String uri, String adsId) {
        this.uri = URI.create(uri);
        this.adsId = adsId;
    }

    public void connect(Consumer<String> onMsg) {
        this.wsClient = new SocketClient(uri, onMsg, adsId);
        this.wsClient.connect();
    }
}
