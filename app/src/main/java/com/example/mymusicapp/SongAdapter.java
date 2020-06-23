package com.example.mymusicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private ArrayList<Song> songs;
    static int lastClickPosition = -1;

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivImg;
        TextView tvTitle, tvArtist;
        ItemClickListener itemClickListener;

        SongViewHolder(View v, ItemClickListener itemClickListener) {
            super(v);
            ivImg = v.findViewById(R.id.ivImg);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvArtist = v.findViewById(R.id.tvArtist);
            this.itemClickListener = itemClickListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public SongAdapter(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent,false);
        return new SongViewHolder(view, (ItemClickListener) parent.getContext());
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.ivImg.setImageResource(R.drawable.dvd);
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

}