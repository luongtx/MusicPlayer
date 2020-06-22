package com.example.mymusicapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    View lastClickView;
    ListView lv_songs;
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
        lv_songs = view.findViewById(R.id.lv_songs);
        ArrayList<Song> songs = ((MainActivity)getActivity()).loadSongs();
        SongAdapter songAdapter = new SongAdapter(view.getContext(), R.layout.song_item, songs);
        lv_songs.setAdapter(songAdapter);

        lv_songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).songPicked(position);
                changeSongItemDisplay(position);
            }
        });

    }

    public void changeSongItemDisplay(int position) {
        View itemview = lv_songs.getChildAt(position - lv_songs.getFirstVisiblePosition());
        itemview.setBackgroundColor(itemview.getResources().getColor(R.color.colorAccent));
        ImageView ivImg = itemview.findViewById(R.id.ivImg);
        Glide.with(this).load(R.drawable.dvd_playing).into(ivImg);
        if(lastClickView != null) {
            lastClickView.setBackgroundColor(itemview.getResources().getColor(R.color.icons));
            ivImg = lastClickView.findViewById(R.id.ivImg);
            ivImg.setImageResource(R.drawable.dvd);
        }
        lastClickView = itemview;
    }


}
