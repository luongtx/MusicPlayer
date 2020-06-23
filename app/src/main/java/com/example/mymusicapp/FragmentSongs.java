package com.example.mymusicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSongs extends Fragment {

    RecyclerView rcv_songs;
    static int lastClickPosition;
    public FragmentSongs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        rcv_songs = view.findViewById(R.id.rcv_songs);
        ArrayList<Song> songs = ((ActivityMain)getActivity()).loadSongs();
        rcv_songs.setHasFixedSize(true);
        rcv_songs.setAdapter(new AdapterSong(songs));
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));
        return view;
    }



    public void changeSongItemDisplay(int position) {
        View view = rcv_songs.getLayoutManager().findViewByPosition(position);
        view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
        Glide.with(view).load(R.drawable.img_dvd_playing).into((ImageView)view.findViewById(R.id.ivSong));

        view = rcv_songs.getLayoutManager().findViewByPosition(lastClickPosition);
        if(view != null && lastClickPosition != position) {
            view.setBackgroundColor(view.getResources().getColor(R.color.icons));
            ImageView imageView = view.findViewById(R.id.ivSong);
            imageView.setImageResource(R.drawable.img_dvd);
        }
        lastClickPosition = position;
    }
}
