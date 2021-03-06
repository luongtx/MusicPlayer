package com.example.mymusicapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusicapp.R;
import com.example.mymusicapp.controller.ActivityMain;
import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.SongViewHolder> implements Filterable {

    private ArrayList<Song> songs;
    private Context context;
    private ArrayList<Song> backup_songs;

    private boolean isMultiSelected = false;
    private int currentPos = -1;
    public AdapterSong(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        backup_songs = new ArrayList<>(songs);
        this.context = context;
    }
    
    
    public void setList(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public void initSelectedSongs() {
        songs.forEach(song -> song.setSelected(false));
        notifyDataSetChanged();
    }

    public interface SongItemClickListener {
        void onSongItemClick(int position);
        void onMultipleSelected();
    }

    public void setMultiSelected(boolean isMultiSelected) {
        this.isMultiSelected = isMultiSelected;
    }

    SongItemClickListener songItemClickListener;

    public void setSongItemClickListener(SongItemClickListener songItemClickListener) {
        this.songItemClickListener = songItemClickListener;
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        View view;
        ImageView ivImg;
        TextView tvTitle, tvArtist;

        SongViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ivImg = itemView.findViewById(R.id.ivSong);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
        }

        public void setOnClickItemListener() {
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (isMultiSelected) {
                Song song = songs.get(getAdapterPosition());
                song.setSelected(!song.isSelected());
                view.setBackgroundColor(song.isSelected() ? context.getResources().getColor(R.color.colorSelected) : Color.WHITE);
            } else {
                try {
                    songItemClickListener.onSongItemClick(getAdapterPosition());
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }



        @Override
        public boolean onLongClick(View v) {
            showPopupMenu(this, getAdapterPosition());
            return true;
        }
    }

    public void showPopupMenu(AdapterSong.SongViewHolder holder, int position) {
        PopupMenu popup = new PopupMenu(context, holder.tvTitle);
        if (((ActivityMain) context).getCurrentPagePosition() == 2) {
            popup.inflate(R.menu.menu_song_playlist_item);
        } else {
            popup.inflate(R.menu.menu_song_item);
        }
        Song song = songs.get(position);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_song:
                    song.setSelected(true);
                    ((ActivityMain)context).deleteFromPlaylist();
                    notifyDataSetChanged();
                    return true;
                case R.id.add_to_playlist:
                    song.setSelected(true);
                    ((ActivityMain)context).onClickOptionAddToPlaylist();
                    return true;
                case R.id.multi_select:
                    song.setSelected(true);
                    setMultiSelected(true);
                    holder.view.setBackgroundColor(context.getResources().getColor(R.color.colorSelected));
                    songItemClickListener.onMultipleSelected();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SongViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @NonNull
    @Override
    public AdapterSong.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.ivImg.setImageResource(R.drawable.img_dvd);
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        holder.view.setBackgroundColor(Color.WHITE);
        holder.setOnClickItemListener();
        if (song.getState() == -1) {
            holder.view.setBackgroundColor(Color.WHITE);
            Glide.with(holder.view).load(R.drawable.img_dvd).into(holder.ivImg);
        } else if (song.getState() == 0) {
            holder.view.setBackgroundColor(Color.CYAN);
            Glide.with(holder.view).load(R.drawable.img_dvd).into(holder.ivImg);
        } else {
            holder.view.setBackgroundColor(Color.CYAN);
            Glide.with(holder.view).load(R.drawable.img_dvd_playing).into(holder.ivImg);
        }
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