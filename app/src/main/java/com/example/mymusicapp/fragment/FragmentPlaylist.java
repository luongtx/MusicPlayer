package com.example.mymusicapp.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterPlayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlaylist extends Fragment {

    RecyclerView rcv_playlists;
    AdapterPlayList adapterPlayList;
    public FragmentPlaylist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        rcv_playlists = view.findViewById(R.id.rcv_playlist);
        adapterPlayList = new AdapterPlayList(ActivityMain.playLists);
        rcv_playlists.setAdapter(adapterPlayList);
        rcv_playlists.setHasFixedSize(true);
        rcv_playlists.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_playlists.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));
        return view;

    }
}
