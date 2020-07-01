package com.example.mymusicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterSong;
import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

import static com.example.mymusicapp.activity.ActivityMain.musicSrv;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtistDetail extends Fragment {

    private RecyclerView rcv_songs;
    private AdapterSong adapterSong;
    private ArrayList<Song> songs;
    public FragmentArtistDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        rcv_songs = view.findViewById(R.id.rcv_songs);
        TextView tvArtist = view.findViewById(R.id.tvArtist);
        String artistName = getArguments().getString("artist");
        tvArtist.setText(artistName);
//        MusicProvider provider = new MusicProvider(getActivity());
        songs = ((ActivityMain)getActivity()).getMusicProvider().loadSongsByArtist(artistName);
        adapterSong = new AdapterSong(songs);
        adapterSong.setModel(((ActivityMain)getActivity()).initModelSelectedItems(songs.size()));
        musicSrv.setList(songs);
        rcv_songs.setAdapter(adapterSong);
        ImageView iv_arrow = view.findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(v -> ((ActivityMain)getActivity()).onBackPressed());

        return view;
    }

    @Override
    public void onDestroy() {
        ActivityMain.songs = ((ActivityMain)getActivity()).getMusicProvider().loadSongs();
        musicSrv.setList(ActivityMain.songs);
        super.onDestroy();
    }

    public AdapterSong getAdapterSong() {
        return adapterSong;
    }
}
