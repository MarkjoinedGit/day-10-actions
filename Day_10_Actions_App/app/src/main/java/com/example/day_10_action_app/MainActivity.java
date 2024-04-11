package com.example.day_10_action_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.day_10_action_app.socket.OrderSocket;
import com.example.day_10_action_app.utility.ServerUtility;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

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

    private OrderSocket orderSocket;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("order", "Order", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Day la thong bao ve don hang");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        fetchAdvertisingId(new AdsIdCallback() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onAdsIdReceived(String adsId) {
                orderSocket = new OrderSocket(ServerUtility.getSocketUrl() + "getOrder", adsId);
                orderSocket.connect((msg) -> {
                    //Log.i("ws", msg);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "order");
                    builder.setSmallIcon(R.color.white);
                    builder.setContentTitle("Your order");
                    builder.setContentText(msg);

                    NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) == 0)
                        manager.notify(1, builder.build());
                });
            }
        });
    }

    public interface AdsIdCallback {
        void onAdsIdReceived(String adsId) throws JSONException;
    }

    public void fetchAdvertisingId(final AdsIdCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());

                    final String adId = adInfo.getId();
                    Log.d("AdvertisingId", "Advertising ID: " + adId);

                    if (adId != null) {
                        if (callback != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        callback.onAdsIdReceived(adId);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e("ID Error", "Cannot get Advertising ID", e);
                }
            }
        }).start();
    }

    public void openUrl(View view) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:12345678"));
        startActivity(urlIntent);
    }

    public void onPlaceOrder(View view) throws JSONException {

        fetchAdvertisingId(new AdsIdCallback() {
           @Override
           public void onAdsIdReceived(String adsId) throws JSONException {
               JSONObject orderJson = new JSONObject();
               orderJson.put("advertisingID", adsId);

               JSONArray orderItemsArray = new JSONArray();

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
       });
    }

    public void postOrder(JSONObject orderJson, UrlRequest.Callback callback) {
        CronetEngine.Builder builder = new CronetEngine.Builder(this.getBaseContext());
        CronetEngine engine = builder.build();

        try {
            UploadDataProvider provider = UploadDataProviders.create(
                    orderJson.toString().getBytes("UTF-8"));

            UrlRequest request = engine.newUrlRequestBuilder(
                            ServerUtility.getServerUrl() + "submit_order",
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