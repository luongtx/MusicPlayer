package com.example.mymusicapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.entity.Playlist;

import java.util.ArrayList;

public class AdapterPlayList extends RecyclerView.Adapter<AdapterPlayList.PlayListHolder> {

    private ArrayList<Playlist> playlists;
    private Context context;
    public AdapterPlayList(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    public interface PlaylistClickListener {
        void onClickPlaylistItem(int position);
    }


    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlayListHolder(view, (PlaylistClickListener) parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayListHolder holder, final int position) {
        final Playlist playList = playlists.get(position);
        holder.tv_name.setText(playList.getName());
        holder.iv_more.setOnClickListener(view -> showPopupMenu(holder, position));
        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(holder, position);
            return true;
        });
    }
    public void showPopupMenu(PlayListHolder holder, int position) {
        PopupMenu popup = new PopupMenu(context, holder.iv_more);
        popup.inflate(R.menu.item_option_menu);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_playlist:
                    ((ActivityMain)context).deletePlaylist(position);
                    notifyDataSetChanged();
                    return true;
                case R.id.add_song:
                    ((ActivityMain)context).onClickOptionAddSongs(position);
                    return true;
                case R.id.rename_playlist:
                    ((ActivityMain)context).updatePlaylistName(position);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }
    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class PlayListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_playlist, iv_more;
        TextView tv_name;
        PlaylistClickListener playlistClickListener;
        public PlayListHolder(@NonNull View itemView, PlaylistClickListener playlistClickListener) {
            super(itemView);
            iv_playlist = itemView.findViewById(R.id.iv_playlist);
            iv_more = itemView.findViewById(R.id.iv_more);
            tv_name = itemView.findViewById(R.id.tv_playlist_name);
            this.playlistClickListener = playlistClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            playlistClickListener.onClickPlaylistItem(getAdapterPosition());
        }
    }
}
