package com.example.mymusicapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

    @NonNull
    @Override
    public PlayListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlayListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayListHolder holder, int position) {
        final Playlist playList = playlists.get(position);
        holder.tv_name.setText(playList.getName());
        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, holder.iv_more);
                popup.inflate(R.menu.item_option_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                ActivityMain.dbMusicHelper.deletePlaylist(playList.getId());
                                notifyDataSetChanged();
                                return true;
                            case R.id.add_song:
                            case R.id.play:
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
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
