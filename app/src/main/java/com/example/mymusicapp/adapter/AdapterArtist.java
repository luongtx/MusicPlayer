package com.example.mymusicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.entity.Artist;
import com.example.mymusicapp.R;

import java.util.ArrayList;

public class AdapterArtist extends RecyclerView.Adapter<AdapterArtist.ArtistHolder> {


    ArrayList<Artist> artists;
    public interface ArtistItemClickListener {
        void onClickArtistItem(int position);
    }

    static class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivArtist;
        TextView tvArtist, tvNoSongs;
        ArtistItemClickListener artistItemClickListener;
        public ArtistHolder(@NonNull View itemView, ArtistItemClickListener artistItemClickListener) {
            super(itemView);
            ivArtist = itemView.findViewById(R.id.ivArtist);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvNoSongs = itemView.findViewById(R.id.tvNumberOfSongs);

            this.artistItemClickListener = artistItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            artistItemClickListener.onClickArtistItem(getAdapterPosition());
        }
    }

    public AdapterArtist(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ArtistHolder(view, (ArtistItemClickListener) parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.ivArtist.setImageResource(R.drawable.img_piano);
        holder.tvArtist.setText(artist.getName());
        holder.tvNoSongs.setText(artist.getNumberOfSongs() + " songs");
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }
}
