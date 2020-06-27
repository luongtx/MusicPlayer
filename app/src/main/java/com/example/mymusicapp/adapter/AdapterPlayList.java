package com.example.mymusicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Playlist;

import java.util.ArrayList;

public class AdapterPlayList extends RecyclerView.Adapter<AdapterPlayList.PlayListHolder> {

    private ArrayList<Playlist> playlists;


    public AdapterPlayList(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlayListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListHolder holder, int position) {
        Playlist playList = playlists.get(position);
        holder.tv_name.setText(playList.getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class PlayListHolder extends RecyclerView.ViewHolder {

        ImageView iv_playlist, iv_more;
        TextView tv_name;
        public PlayListHolder(@NonNull View itemView) {
            super(itemView);
            iv_playlist = itemView.findViewById(R.id.iv_playlist);
            iv_more = itemView.findViewById(R.id.iv_more);
            tv_name = itemView.findViewById(R.id.tv_playlist_name);
        }
    }
}
