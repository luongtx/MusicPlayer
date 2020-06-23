package com.example.mymusicapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFrag extends Fragment {

    RecyclerView rcv_songs;
    static int lastClickPosition;
    public SongsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View song_view = inflater.inflate(R.layout.fragment_songs, container, false);
        return song_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv_songs = view.findViewById(R.id.rcv_songs);
        ArrayList<Song> songs = ((MainActivity)getActivity()).loadSongs();
        rcv_songs.setHasFixedSize(true);
        rcv_songs.setAdapter(new SongAdapter(songs));
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public void changeSongItemDisplay(int position) {
        View view = rcv_songs.getLayoutManager().findViewByPosition(position);
        view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
        Glide.with(view).load(R.drawable.dvd_playing).into((ImageView)view.findViewById(R.id.ivImg));

        view = rcv_songs.getLayoutManager().findViewByPosition(lastClickPosition);
        if(view != null && lastClickPosition != position) {
            view.setBackgroundColor(view.getResources().getColor(R.color.icons));
            ImageView imageView = view.findViewById(R.id.ivImg);
            imageView.setImageResource(R.drawable.dvd);
        }
        lastClickPosition = position;
    }
}
