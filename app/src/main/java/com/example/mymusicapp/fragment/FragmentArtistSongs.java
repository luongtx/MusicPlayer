package com.example.mymusicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterSong;
import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.example.mymusicapp.activity.ActivityMain.musicSrv;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtistSongs extends Fragment {

    RecyclerView rcv_artist_songs;
    AdapterSong adapterSong;
    ArrayList<Song> songs;
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
        String artistName = getArguments().getString("artist");
        tvArtist.setText(artistName);

        songs = ((ActivityMain)getActivity()).getMusicProvider().loadSongsByArtist(artistName);
        ActivityMain.songs = songs;
        adapterSong = new AdapterSong(songs, getContext());
        adapterSong.setModel(((ActivityMain)getActivity()).initModelSelectedItems(songs.size()));
        musicSrv.setList(songs);
        rcv_artist_songs.setAdapter(adapterSong);
        rcv_artist_songs.setHasFixedSize(true);
        rcv_artist_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_artist_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));

        ImageView iv_arrow = view.findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(v -> ((ActivityMain)getActivity()).onBackPressed());
        return view;
    }

//    @Override
//    public void onDestroy() {
//        ActivityMain.songs = ((ActivityMain)getActivity()).getMusicProvider().loadSongs();
//        musicSrv.setList(ActivityMain.songs);
//        super.onDestroy();
//    }

    public AdapterSong getAdapterSong() {
        return adapterSong;
    }
}
