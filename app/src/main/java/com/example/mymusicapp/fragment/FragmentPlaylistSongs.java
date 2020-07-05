package com.example.mymusicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterSong;
import com.example.mymusicapp.entity.Playlist;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.repository.DBMusicHelper;

import java.util.ArrayList;

import static com.example.mymusicapp.activity.ActivityMain.musicSrv;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlaylistSongs extends Fragment {

    RecyclerView rcv_songs;
    AdapterSong adapterSong;
    ArrayList<Song> songs;
    int playlist_pos;
    public FragmentPlaylistSongs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_details, container, false);
        TextView tv_playlist_name = view.findViewById(R.id.tv_playlist_name);
        rcv_songs = view.findViewById(R.id.rcv_songs);

        playlist_pos = getArguments().getInt("playlist_pos");
        Playlist playlist = ActivityMain.playLists.get(playlist_pos);
        int playlistId = playlist.getId();
        tv_playlist_name.setText(playlist.getName());

        DBMusicHelper dbMusicHelper = new DBMusicHelper(getActivity());
        songs = dbMusicHelper.getPlaylistSongs(playlistId);
        adapterSong = new AdapterSong(songs, getContext());
        adapterSong.setModel(((ActivityMain)getActivity()).initModelSelectedItems(songs.size()));
        musicSrv.setList(songs);
        rcv_songs.setAdapter(adapterSong);
        ImageView iv_arrow = view.findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(v -> ((ActivityMain)getActivity()).onBackPressed());
        ImageView btn_plus = view.findViewById(R.id.btn_plus_song);
        btn_plus.setOnClickListener(v -> ((ActivityMain)getActivity()).onClickOptionAddSongs(playlist_pos));
        return view;
    }

    public int getPlaylist_pos() {
        return playlist_pos;
    }

    public void setPlaylist_pos(int playlist_pos) {
        this.playlist_pos = playlist_pos;
    }

    public AdapterSong getAdapterSong() {
        return adapterSong;
    }

    public void setAdapterSong(AdapterSong adapterSong) {
        this.adapterSong = adapterSong;
    }
}
