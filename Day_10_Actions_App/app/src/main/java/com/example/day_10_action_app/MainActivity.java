package com.example.day_10_action_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

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
}