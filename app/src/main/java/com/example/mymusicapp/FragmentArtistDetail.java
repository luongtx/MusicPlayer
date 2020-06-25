package com.example.mymusicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtistDetail extends Fragment {

    RecyclerView rcv_songs;
    public FragmentArtistDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);

        TextView tvArtist = view.findViewById(R.id.tvArtist);
        String artistName = getArguments().getString("artist");
        tvArtist.setText(artistName);
        rcv_songs = view.findViewById(R.id.rcv_songs);
        rcv_songs = view.findViewById(R.id.rcv_songs);

        ArrayList<Song> songs = ActivityMain.musicProvider.loadSongsByArtist(artistName);
        rcv_songs.setAdapter(new AdapterSong(songs));
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));
        return view;
    }
}
