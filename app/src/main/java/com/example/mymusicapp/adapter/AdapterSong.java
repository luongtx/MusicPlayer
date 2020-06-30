package com.example.mymusicapp.adapter;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.model.ModelSelectedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.SongViewHolder> implements Filterable {

    private ArrayList<Song> songs;
    private ArrayList<Song> backup_songs;
    private ArrayList<ModelSelectedItem> modelSelectedItems;

    private boolean isMultiSelected = false;
    private boolean isLongClicked = false;

    public AdapterSong(ArrayList<Song> songs) {
        this.songs = songs;
        backup_songs = new ArrayList<>(songs);
    }

    public interface SongItemClickListeneer {
        void onSongItemClick(int position);
        void onSongItemLongClicked();
    }

    public void setMultiSelected(boolean isMultiSelected) {
        this.isMultiSelected = isMultiSelected;
    }

    public void setLongClicked(boolean longClicked) {
        isLongClicked = longClicked;
    }

    public void setModel(ArrayList<ModelSelectedItem> modelSelectedItems) {
        this.modelSelectedItems = modelSelectedItems;
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
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
            this.songItemClickListeneer = songItemClickListeneer;
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (isMultiSelected || isLongClicked) {
                ModelSelectedItem modelSelectedItem = modelSelectedItems.get(getAdapterPosition());
                modelSelectedItem.setSelectd(!modelSelectedItem.isSelectd());
                view.setBackgroundColor(modelSelectedItem.isSelectd() ? Color.CYAN : Color.WHITE);
            } else {
                songItemClickListeneer.onSongItemClick(getAdapterPosition());
            }

        }

        @Override
        public boolean onLongClick(View v) {
            songItemClickListeneer.onSongItemLongClicked();
            AdapterSong.this.setLongClicked(true);
            ModelSelectedItem modelSelectedItem = modelSelectedItems.get(getAdapterPosition());
            modelSelectedItem.setSelectd(!modelSelectedItem.isSelectd());
            view.setBackgroundColor(modelSelectedItem.isSelectd() ? Color.CYAN : Color.WHITE);
            return true;
        }
    }

    @NonNull
    @Override
    public AdapterSong.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent,false);
        return new SongViewHolder(view, (SongItemClickListeneer) parent.getContext());
    }

    @Override
    public void onBindViewHolder(final SongViewHolder holder, int position) {
        holder.ivImg.setImageResource(R.drawable.img_dvd);
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Song> filteredList;
            if (constraint == null || constraint.length() == 0) {
                filteredList = new ArrayList<>(backup_songs);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                filteredList = backup_songs.stream()
                        .filter((item) -> item.getTitle().toLowerCase().contains(filterPattern))
                        .collect(Collectors.toList());
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songs.clear();
            songs.addAll((ArrayList<Song>) results.values);
            notifyDataSetChanged();
        }
    };
}