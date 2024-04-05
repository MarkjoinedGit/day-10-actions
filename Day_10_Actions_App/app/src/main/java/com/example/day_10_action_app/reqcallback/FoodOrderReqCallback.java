package com.example.day_10_action_app.reqcallback;

import android.util.Log;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Consumer;

public class FoodOrderReqCallback extends UrlRequest.Callback {
    private final Consumer<JSONObject> handler;
    private String result;

    public FoodOrderReqCallback(Consumer<JSONObject> handler) {
        this.handler = handler;
    }


    @Override
    public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {

    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {
        if(info.getHttpStatusCode() == 200) {
            request.read(ByteBuffer.allocateDirect(10240));
        }
    }

    @Override
    public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes); //nap het du lieu tu con tro vo bytes
        this.result = new String(bytes);
        Log.i(this.getClass().getSimpleName(), this.result);
        byteBuffer.clear();
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        Log.i(this.getClass().getSimpleName(), "Da lay thanh cong du lieu");

        try {
            JSONObject jsonObject = new JSONObject(this.result);
            this.handler.accept(jsonObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        Log.e(this.getClass().getSimpleName(), Objects.requireNonNull(error.getMessage()));
    }
}
