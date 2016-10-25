package com.example.phompang.eatyfinder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phompang.eatyfinder.R;
import com.example.phompang.eatyfinder.model.Genre;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phompang on 10/25/2016 AD.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private ArrayList<Genre> genres;
    private Context mContext;

    public GenreAdapter(Context context, ArrayList<Genre> genres) {
        this.genres = genres;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.genre_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mImg.setImageResource(genres.get(position).getPhoto());
        holder.mText.setText(genres.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.genreImg)
        ImageView mImg;
        @BindView(R.id.genreText)
        TextView mText;
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
