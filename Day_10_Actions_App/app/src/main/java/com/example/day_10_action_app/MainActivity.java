package com.example.day_10_action_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void openUrl(View view) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:12345678"));
        startActivity(urlIntent);

    }

    public void onPlaceOrder(View view) throws JSONException {
        JSONObject orderJson = new JSONObject();

        // Thêm trường advertisingId
        orderJson.put("advertisingID", "ABC123456");

        // Tạo một mảng JSONArray cho OrderItems
        JSONArray orderItemsArray = new JSONArray();


        // Tạo đối tượng JSONObject cho mỗi mục hàng trong đơn hàng và thêm vào mảng
        JSONObject item1 = new JSONObject();
        item1.put("name", "phobo");
        orderItemsArray.put(item1);

        orderJson.put("items", orderItemsArray);

        postOrder(orderJson, new UrlRequest.Callback() {
            @Override
            public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {

            }

            @Override
            public void onResponseStarted(UrlRequest request, UrlResponseInfo info) throws Exception {

            }

            @Override
            public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) throws Exception {

            }

            @Override
            public void onSucceeded(UrlRequest request, UrlResponseInfo info) {

            }

            @Override
            public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {

            }
        });
    }

    public void postOrder(JSONObject orderJson, UrlRequest.Callback callback) {
        CronetEngine.Builder builder = new CronetEngine.Builder(this.getBaseContext());
        CronetEngine engine = builder.build();

        try {
            UploadDataProvider provider = UploadDataProviders.create(
                    orderJson.toString().getBytes("UTF-8"));

            UrlRequest request = engine.newUrlRequestBuilder(
                            "http://172.30.64.1:8080/submit_order",
                            callback,
                            Executors.newSingleThreadExecutor())
                    .setHttpMethod("POST")
                    .addHeader("Content-Type", "application/json")
                    .setUploadDataProvider(provider, Executors.newSingleThreadExecutor())
                    .build();

            request.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}