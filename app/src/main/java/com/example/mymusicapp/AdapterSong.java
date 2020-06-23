package com.example.mymusicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.SongViewHolder> {

    private ArrayList<Song> songs;

    public interface SongItemClickListeneer {
        void onSongItemClick(int position);
    }

    static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivImg;
        TextView tvTitle, tvArtist;
        SongItemClickListeneer songItemClickListeneer;

        SongViewHolder(View v, SongItemClickListeneer songItemClickListeneer) {
            super(v);
            ivImg = v.findViewById(R.id.ivSong);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvArtist = v.findViewById(R.id.tvArtist);
            this.songItemClickListeneer = songItemClickListeneer;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            songItemClickListeneer.onSongItemClick(getAdapterPosition());
        }
    }

    public AdapterSong(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public AdapterSong.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent,false);
        return new SongViewHolder(view, (SongItemClickListeneer) parent.getContext());
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.ivImg.setImageResource(R.drawable.img_dvd);
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

}