package com.example.mymusicapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.model.ModelSelectedItem;

import java.util.ArrayList;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.SongViewHolder> {

    private ArrayList<Song> songs;
    private ArrayList<ModelSelectedItem> modelSelectedItems;

    private boolean isMultiSelected = false;

    public AdapterSong(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public interface SongItemClickListeneer {
        void onSongItemClick(int position);
    }

    public void setMultiSelected(boolean isMultiSelected) {
        this.isMultiSelected = isMultiSelected;
    }

    public void setList(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setModel(ArrayList<ModelSelectedItem> modelSelectedItems) {
        this.modelSelectedItems = modelSelectedItems;
    }

    static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View view;
        ImageView ivImg;
        TextView tvTitle, tvArtist;
        SongItemClickListeneer songItemClickListeneer;

        SongViewHolder(View itemView, SongItemClickListeneer songItemClickListeneer) {
            super(itemView);
            this.view = itemView;
            ivImg = itemView.findViewById(R.id.ivSong);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            if(songItemClickListeneer != null) {
                this.songItemClickListeneer = songItemClickListeneer;
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            songItemClickListeneer.onSongItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public AdapterSong.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent,false);
        if(!isMultiSelected) return new SongViewHolder(view, (SongItemClickListeneer) parent.getContext());
        else return new SongViewHolder(view,null);

    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        holder.ivImg.setImageResource(R.drawable.img_dvd);
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        if(isMultiSelected) {
            final ModelSelectedItem modelSelectedItem = modelSelectedItems.get(position);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modelSelectedItem.setSelectd(!modelSelectedItem.isSelectd());
                    holder.view.setBackgroundColor(modelSelectedItem.isSelectd() ? Color.CYAN : Color.WHITE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

}