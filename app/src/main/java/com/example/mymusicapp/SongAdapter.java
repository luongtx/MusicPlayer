package com.example.mymusicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter {

    private ArrayList<Song> songs;
    private Context context;
    private int resource;

    public SongAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Song> songs) {
        super(context, resource, songs);
        this.context = context;
        this.resource = resource;
        this.songs = songs;
    }
    private class SongViewHolder {
        ImageView ivImg;
        TextView tvTitle, tvArtist;
        SongViewHolder(View v) {
            ivImg = v.findViewById(R.id.ivImg);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvArtist = v.findViewById(R.id.tvArtist);
        }
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SongViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new SongViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SongViewHolder) convertView.getTag();
        }
        Song song = songs.get(position);
        viewHolder.ivImg.setImageResource(R.drawable.dvd);
        viewHolder.tvTitle.setText(song.getTitle());
        viewHolder.tvArtist.setText(song.getArtist());
        return convertView;
    }
}