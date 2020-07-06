package com.example.mymusicapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Artist;

import java.util.ArrayList;

public class AdapterArtist extends RecyclerView.Adapter<AdapterArtist.ArtistHolder> {


    ArrayList<Artist> artists;
    public interface ArtistItemClickListener {
        void onClickArtistItem(int position);
    }

    ArtistItemClickListener artistItemClickListener;

    public void setArtistItemClickListener(ArtistItemClickListener artistItemClickListener) {
        this.artistItemClickListener = artistItemClickListener;
    }

    class ArtistHolder extends RecyclerView.ViewHolder {

        ImageView ivArtist;
        TextView tvArtist, tvNoSongs;
        View view;
        public ArtistHolder(@NonNull View itemView) {
            super(itemView);
            ivArtist = itemView.findViewById(R.id.ivArtist);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvNoSongs = itemView.findViewById(R.id.tvNumberOfSongs);
            view = itemView;
        }

        public void setOnClickItemListener() {
            view.setOnClickListener(v -> artistItemClickListener.onClickArtistItem(getAdapterPosition()));
        }
    }

    Context context;
    public AdapterArtist(ArrayList<Artist> artists, Context context) {
        this.artists = artists;
        this.context = context;
    }

    public void setList(ArrayList<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.ivArtist.setImageResource(R.drawable.img_piano);
        holder.tvArtist.setText(artist.getName());
        holder.tvNoSongs.setText(artist.getNumberOfSongs() + " " + context.getString(R.string.songs));
        holder.setOnClickItemListener();
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }
}
