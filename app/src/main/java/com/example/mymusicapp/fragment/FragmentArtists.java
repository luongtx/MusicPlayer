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
import com.example.mymusicapp.adapter.AdapterArtist;
import com.example.mymusicapp.entity.Artist;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtists extends Fragment{

    RecyclerView rcvArtist;
    AdapterArtist adapterArtist;
    ArrayList<Artist> artists;
    Context context;
    public FragmentArtists() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        rcvArtist = view.findViewById(R.id.rcv_artist);
        artists = ((ActivityMain) context).getArtists();
        adapterArtist = new AdapterArtist(artists, getContext());
        adapterArtist.setArtistItemClickListener(this::onArtistItemClicked);
        rcvArtist.setAdapter(adapterArtist);
        rcvArtist.setLayoutManager(new LinearLayoutManager(context));
        rcvArtist.addItemDecoration(new DividerItemDecoration(context, HORIZONTAL));
        return view;
    }

    public AdapterArtist getAdapterArtist() {
        return adapterArtist;
    }

    public void onArtistItemClicked(int position) {
        String artistName = artists.get(position).getName();
        FragmentArtistSongs fragmentArtistSongs = ((ActivityMain)context).newArtistSongFragment();
        Bundle bundle = new Bundle();
        bundle.putString("artist", artistName);
        fragmentArtistSongs.setArguments(bundle);
        ((ActivityMain) context).replace_fragment(false,1, fragmentArtistSongs);
        ((ActivityMain) context).setIconForTabTitle();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
