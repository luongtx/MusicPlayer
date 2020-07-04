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

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.model.ModelSelectedItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.SongViewHolder> implements Filterable {

    private ArrayList<Song> songs;
    private Context context;
    private ArrayList<Song> backup_songs;
    private ArrayList<ModelSelectedItem> modelSelectedItems;

    private boolean isMultiSelected = false;
    private boolean isLongClicked = false;

    public AdapterSong(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        backup_songs = new ArrayList<>(songs);
        this.context = context;
    }

    public void setList(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public interface SongItemClickListener {
        void onSongItemClick(int position);
        void onMultipleSelected();
    }

    public void setMultiSelected(boolean isMultiSelected) {
        this.isMultiSelected = isMultiSelected;
    }

    public void setLongClicked(boolean longClicked) {
        isLongClicked = longClicked;
    }

    public void setModel(ArrayList<ModelSelectedItem> modelSelectedItems) {
        this.modelSelectedItems = modelSelectedItems;
        notifyDataSetChanged();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        View view;
        ImageView ivImg;
        TextView tvTitle, tvArtist;
        SongItemClickListener songItemClickListener;

        SongViewHolder(View itemView, SongItemClickListener songItemClickListener) {
            super(itemView);
            this.view = itemView;
            ivImg = itemView.findViewById(R.id.ivSong);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            this.songItemClickListener = songItemClickListener;
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (isMultiSelected) {
                ModelSelectedItem modelSelectedItem = modelSelectedItems.get(getAdapterPosition());
                modelSelectedItem.setSelectd(!modelSelectedItem.isSelectd());
                view.setBackgroundColor(modelSelectedItem.isSelectd() ? Color.CYAN : Color.WHITE);
            } else {
                songItemClickListener.onSongItemClick(getAdapterPosition());
            }

        }

        @Override
        public boolean onLongClick(View v) {
            ModelSelectedItem modelSelectedItem = modelSelectedItems.get(getAdapterPosition());
            modelSelectedItem.setSelectd(!modelSelectedItem.isSelectd());
            showPopupMenu(this, getAdapterPosition());
            AdapterSong.this.setLongClicked(true);
            return true;
        }
    }

    public void showPopupMenu(AdapterSong.SongViewHolder holder, int position) {
        PopupMenu popup = new PopupMenu(context, holder.tvTitle);
        if(((ActivityMain)context).getCurrentPagePosition() ==2) {
            popup.inflate(R.menu.menu_song_playlist_item);
        }else {
            popup.inflate(R.menu.menu_song_item);
        }
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_song:
                    ((ActivityMain)context).deleteFromPlaylist();
                    notifyDataSetChanged();
                    return true;
                case R.id.add_to_playlist:
                    ((ActivityMain)context).onClickOptionAddToPlaylist();
                    return true;
                case R.id.multi_select:
                    modelSelectedItems.get(position).setSelectd(true);
                    setMultiSelected(true);
                    holder.view.setBackgroundColor(context.getResources().getColor(R.color.colorSelected));
                    ((ActivityMain)context).changeMenuWhenSelectMultipleItem();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent,false);
        return new SongViewHolder(view, (SongItemClickListener) parent.getContext());
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.ivImg.setImageResource(R.drawable.img_dvd);
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvArtist.setText(song.getArtist());
        holder.view.setBackgroundColor(Color.WHITE);
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