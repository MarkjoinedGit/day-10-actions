package com.example.day_10_action_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.day_10_action_app.fragment.FoodOrderFragment;
import com.example.day_10_action_app.fragment.placeholder.PlaceholderContent;
import com.example.day_10_action_app.socket.OrderSocket;
import com.example.day_10_action_app.utility.ServerUtility;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private OrderSocket orderSocket;
    private final int FOOD_NAME_INDEX = 0;
    private final int FOOD_NAME_COUNT = 2;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer,
                R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        Runnable addShortcutRunnable = createShortcutAboutUs();

        // Tạo một luồng công việc mới và chạy Runnable trên đó
        new Thread(addShortcutRunnable).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("order", "Order", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Day la thong bao ve don hang");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        fetchAdvertisingId(adsId -> {
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
        });
    }

    public interface AdsIdCallback {
        void onAdsIdReceived(String adsId) throws JSONException;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public Runnable createShortcutAboutUs() {
        return () -> {
            Intent aboutIntent = new Intent(MainActivity.this, AboutUsActivity.class);
            aboutIntent.setAction(Intent.ACTION_MAIN);
            aboutIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, aboutIntent, PendingIntent.FLAG_IMMUTABLE);

            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(MainActivity.this, "about_shortcut")
                    .setShortLabel("Về chúng tôi")
                    .setIcon(Icon.createWithResource(MainActivity.this, R.mipmap.ic_launcher))
                    .setIntent(aboutIntent)
                    .build();
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            shortcutManager.setDynamicShortcuts(Collections.singletonList(shortcutInfo));
        };
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

    public void onConfirmPlaceOrder(View view) {
        FoodOrderFragment fragment = (FoodOrderFragment) getSupportFragmentManager().findFragmentById(R.id.food_list);

        if (fragment != null) {
            List<PlaceholderContent.PlaceholderItem> items = fragment.getItems();
            Log.i("items", items.toString());
            StringBuilder listItem = new StringBuilder();
            for (PlaceholderContent.PlaceholderItem item : items) {
                int count = Integer.parseInt(item.foodDetail.get(FOOD_NAME_COUNT));
                if (count > 0) {
                    listItem.append(item.foodDetail.get(FOOD_NAME_INDEX)).append(" x").append(item.foodDetail.get(FOOD_NAME_COUNT)).append(" \n");
                }
            }
            MaterialAlertDialogBuilder builder =
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Confirm your order")
                            .setMessage(listItem)
                            .setPositiveButton("Yes", (dialog, which) -> {
                                        try {
                                            onPlaceOrder(view);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            )
                            .setNegativeButton("No", (dialog, which) -> getSnackBar());
            builder.show();
        }
    }

    private void getSnackBar() {
        Snackbar.make(findViewById(R.id.MainLayout), "Cancel order", Snackbar.LENGTH_LONG).show();
    }

    public void onPlaceOrder(View view) throws JSONException {


        fetchAdvertisingId(new AdsIdCallback() {
           @Override
           public void onAdsIdReceived(String adsId) throws JSONException {
               FoodOrderFragment fragment = (FoodOrderFragment) getSupportFragmentManager().findFragmentById(R.id.food_list);

               if (fragment != null) {
                   List<PlaceholderContent.PlaceholderItem> items = fragment.getItems();
                   Log.i("items", items.toString());
                   JSONObject orderJson = new JSONObject();
                   orderJson.put("advertisingID", adsId);
                   JSONArray orderItemsArray = new JSONArray();

                   for(PlaceholderContent.PlaceholderItem item:items) {
                       int count = Integer.parseInt(item.foodDetail.get(FOOD_NAME_COUNT));
                       if(count > 0) {
                           JSONObject itemJS = new JSONObject();
                           itemJS.put("name", item.foodDetail.get(FOOD_NAME_INDEX));
                           itemJS.put("count", item.foodDetail.get(FOOD_NAME_COUNT));
                           orderItemsArray.put(itemJS);
                           Log.i("orders", orderItemsArray.toString());
                       }
                   }

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
           }
       });
    }

    public void postOrder(JSONObject orderJson, UrlRequest.Callback callback) {
        CronetEngine.Builder builder = new CronetEngine.Builder(this.getBaseContext());
        CronetEngine engine = builder.build();

        UploadDataProvider provider = UploadDataProviders.create(
                orderJson.toString().getBytes(StandardCharsets.UTF_8));

        UrlRequest request = engine.newUrlRequestBuilder(
                        ServerUtility.getServerUrl() + "submit_order",
                        callback,
                        Executors.newSingleThreadExecutor())
                .setHttpMethod("POST")
                .addHeader("Content-Type", "application/json")
                .setUploadDataProvider(provider, Executors.newSingleThreadExecutor())
                .build();

        request.start();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about_us) {
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (item.getItemId() == R.id.feedback) {
            Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:foodorder@cntt.io"));
            startActivity(urlIntent);
        }
        return false;
    }
}