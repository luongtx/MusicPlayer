package com.example.mymusicapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSongs extends Fragment{

    private RecyclerView rcv_songs;
    private AdapterSong adapterSong;
    private ArrayList<Song> songs;
    public FragmentSongs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        rcv_songs = view.findViewById(R.id.rcv_songs);
        songs = ActivityMain.songs;
        ActivityMain.setList(songs);
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
        rcv_songs.setAdapter(adapterSong);
        rcv_songs.setHasFixedSize(true);
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));
        return view;
    }

    public void setAdapterSong(AdapterSong adapterSong) {
        this.adapterSong = adapterSong;
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
}