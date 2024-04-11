package com.example.day_10_action_app.fragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.day_10_action_app.R;
import com.example.day_10_action_app.fragment.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.day_10_action_app.databinding.FragmentFoodOrderBinding;
import com.example.day_10_action_app.utility.ServerUtility;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;
    private Fragment mFragment;

    public MyItemRecyclerViewAdapter(List<PlaceholderItem> items, Fragment fragment) {

        mValues = items;
        mFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentFoodOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mFoodName.setText(mValues.get(position).foodDetail.get(0));
        holder.mCount.setText(mValues.get(position).foodDetail.get(2));
        String thumbUrl = ServerUtility.getServerUrl() + mValues.get(position).foodDetail.get(1);
        Glide.with(mFragment)
                .load(thumbUrl)
                .into(holder.mThumbnail);holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);

        holder.mMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt((String) holder.mCount.getText());
                if(count > 0) {
                    count-=1;
                    holder.mCount.setText(String.valueOf(count));
                    mValues.get(holder.getAdapterPosition()).foodDetail.set(2, String.valueOf(count));
                }
            }
        });

        holder.mPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt((String) holder.mCount.getText());
                count+=1;
                holder.mCount.setText(String.valueOf(count));
                mValues.get(holder.getAdapterPosition()).foodDetail.set(2, String.valueOf(count));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<PlaceholderItem> getItems() {
        return mValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public  final TextView mFoodName;
        public PlaceholderItem mItem;
        public ImageView mThumbnail;
        public Button mMinusBtn;
        public Button mPlusBtn;
        public TextView mCount;

        public ViewHolder(FragmentFoodOrderBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mFoodName = binding.foodName;
            mThumbnail = binding.foodThumbnail;
            mMinusBtn = binding.minusBtn;
            mPlusBtn = binding.plusBtn;
            mCount = binding.countFood;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFoodName.getText() + "'";
        }
    }
}