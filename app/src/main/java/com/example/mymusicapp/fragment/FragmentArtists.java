package com.example.mymusicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterArtist;
import com.example.mymusicapp.entity.Artist;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtists extends Fragment {

    RecyclerView rcvArtist;
    AdapterArtist adapterArtist;
    public FragmentArtists() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        rcvArtist = view.findViewById(R.id.rcv_artist);
        ArrayList<Artist> artists = ActivityMain.artists;
        adapterArtist = new AdapterArtist(artists);
        rcvArtist.setAdapter(adapterArtist);
        rcvArtist.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcvArtist.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));
        return view;
    }

    public AdapterArtist getAdapterArtist() {
        return adapterArtist;
    }
}
