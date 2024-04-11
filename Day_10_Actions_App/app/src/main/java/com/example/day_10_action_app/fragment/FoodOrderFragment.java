package com.example.day_10_action_app.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.day_10_action_app.R;
import com.example.day_10_action_app.fragment.placeholder.PlaceholderContent;
import com.example.day_10_action_app.reqcallback.FoodOrderReqCallback;
import com.example.day_10_action_app.utility.ServerUtility;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * A fragment representing a list of Items.
 */
public class FoodOrderFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private MyItemRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoodOrderFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FoodOrderFragment newInstance(int columnCount) {
        FoodOrderFragment fragment = new FoodOrderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_order_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS, this);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CronetEngine.Builder builder = new CronetEngine.Builder(this.requireContext());
        CronetEngine engine = builder.build();

        UrlRequest urlReq = engine.newUrlRequestBuilder(ServerUtility.getServerUrl() + "food",
                        new FoodOrderReqCallback(
                                (jsonObject -> {
                                    try {
                                        JSONArray placesArray = jsonObject.getJSONArray("food");
                                        for (int i = 0; i < placesArray.length(); i++) {
                                            JSONObject placeObject = placesArray.getJSONObject(i);
                                            List<String> foodDetail = new ArrayList<>();
                                            int id = placeObject.getInt("id");
                                            String food_name = placeObject.getString("name");
                                            String thumbUrl = placeObject.getString("image");
                                            foodDetail.add(food_name);
                                            foodDetail.add(thumbUrl);
                                            foodDetail.add("0");
                                            PlaceholderContent.addItem(PlaceholderContent.createPlaceholderItem(id, foodDetail));
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        ), Executors.newSingleThreadExecutor())
                .setHttpMethod("GET").build();

        urlReq.start();
    }

    public List<PlaceholderContent.PlaceholderItem> getItems() {
        return adapter.getItems();
    }

}