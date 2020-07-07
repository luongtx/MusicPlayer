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
import com.example.mymusicapp.controller.ActivityMain;
import com.example.mymusicapp.adapter.AdapterSong;
import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.example.mymusicapp.controller.ActivityMain.songs;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtistSongs extends Fragment {

    RecyclerView rcv_artist_songs;
    AdapterSong adapterSong;
    String artistName;
    public FragmentArtistSongs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_songs, container, false);
        rcv_artist_songs = view.findViewById(R.id.rcv_artist_songs);
        TextView tvArtist = view.findViewById(R.id.tvArtist);
        artistName = getArguments().getString("artist");
        tvArtist.setText(artistName);

        songs = ((ActivityMain) context).loadSongsByArtist(artistName);
//        ActivityMain.setList(songs);
        adapterSong = new AdapterSong(songs, getContext());
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
        rcv_artist_songs.setAdapter(adapterSong);
        rcv_artist_songs.setHasFixedSize(true);
        rcv_artist_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_artist_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));

        ImageView iv_arrow = view.findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(v -> ((ActivityMain)context).onBackPressed());
        return view;
    }

    public AdapterSong getAdapterSong() {
        return adapterSong;
    }

    public void notifySongStateChanges(ArrayList<Song> songs) {
        adapterSong.setList(songs);
    }

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public String getArtistName() {
        return artistName;
    }
}
