package com.example.mymusicapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterSong;
import com.example.mymusicapp.entity.Playlist;
import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.example.mymusicapp.activity.ActivityMain.songs;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlaylistSongs extends Fragment {

    RecyclerView rcv_playlist_songs;
    AdapterSong adapterSong;
    int playlist_pos;
    public FragmentPlaylistSongs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_songs, container, false);
        TextView tv_playlist_name = view.findViewById(R.id.tv_playlist_name);
        rcv_playlist_songs = view.findViewById(R.id.rcv_playlist_songs);

        playlist_pos = getArguments().getInt("playlist_pos");
        Playlist playlist = ActivityMain.playLists.get(playlist_pos);
        int playlistId = playlist.getId();
        tv_playlist_name.setText(playlist.getName());

        songs = ((ActivityMain) context).getPlaylistSongs(playlistId);
        ActivityMain.setList(songs);
        setPlaylistSongs(songs);
        rcv_playlist_songs.setAdapter(adapterSong);
        rcv_playlist_songs.setHasFixedSize(true);
        rcv_playlist_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_playlist_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));

        ImageView iv_arrow = view.findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(v -> ((ActivityMain)context).onBackPressed());
        ImageView btn_plus = view.findViewById(R.id.btn_plus_song);
        btn_plus.setOnClickListener(v -> ((ActivityMain) context).onClickOptionAddSongs(playlist_pos));
        return view;
    }

    public int getPlaylist_pos() {
        return playlist_pos;
    }

    public AdapterSong getAdapterSong() {
        return adapterSong;
    }

    public void setAdapterSong(AdapterSong adapterSong) {
        this.adapterSong = adapterSong;
    }

    public void notifySongStateChanges(ArrayList<Song> songs) {
        adapterSong.setList(songs);
    }

    public void deleteFromPlaylist() {
        ArrayList<Song> selectedSongs = new ArrayList<>();
        for (Song song : songs) {
            if (song.isSelected()) {
                selectedSongs.add(song);
            }
        }
        int playlistId = ActivityMain.playLists.get(getPlaylist_pos()).getId();
        ((ActivityMain)context).deleteSongsFromPlaylist(playlistId, selectedSongs);
        songs = ((ActivityMain) context).getPlaylistSongs(playlistId);
        adapterSong.setList(songs);
    }

    public void setPlaylistSongs(ArrayList<Song> playlistSongs) {
        if (adapterSong != null) {
            adapterSong.setList(playlistSongs);
        } else {
            adapterSong = new AdapterSong(playlistSongs, getContext());
        }
        adapterSong.initSelectedSongs();
        adapterSong.setSongItemClickListener(new AdapterSong.SongItemClickListener() {
            @Override
            public void onSongItemClick(int position) {
                ((ActivityMain) context).pickSong(position);
            }
            @Override
            public void onMultipleSelected() {
                ((ActivityMain) context).changeMenuWhenSelectMultipleItem();
            }
        });
    }
    
    Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
