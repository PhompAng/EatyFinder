package com.example.phompang.eatyfinder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.R;
import com.example.phompang.eatyfinder.model.Icon;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fi.foyt.foursquare.api.entities.Category;

/**
 * Created by phompang on 12/2/2016 AD.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context mContext;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.categories = categories;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.category_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (categories.get(position) != null) {
            Gson gson = new Gson();
            Icon icon = gson.fromJson(categories.get(position).getIcon(), Icon.class);
            Log.d("icon", icon.getPrefix() + "88" + icon.getSuffix());
            Glide.with(mContext).load(icon.getPrefix() + "88" + icon.getSuffix()).centerCrop().into(holder.mImg);
            holder.mText.setText(categories.get(position).getName());
        } else {
            // make sure Glide doesn't load anything into this view until told otherwise
            Glide.clear(holder.mImg);
            // remove the placeholder (optional); read comments below
            holder.mImg.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.categoryImg)
        ImageView mImg;
        @BindView(R.id.categoryText)
        TextView mText;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
