package com.example.mymusicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
public class FragmentSelectSongs extends Fragment {

    private RecyclerView rcv_songs;
    private AdapterSong adapterSong;
    private ImageView iv_arrow_back;
    private ImageView iv_confirm;
    public FragmentSelectSongs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_songs, container, false);
        rcv_songs = view.findViewById(R.id.rcv_songs);

        ArrayList<Song> songs = ((ActivityMain)getActivity()).getMusicProvider().loadSongs();
        adapterSong = new AdapterSong(songs);
        adapterSong.setModel(((ActivityMain)getActivity()).initModelSelectedItems(songs.size()));
        adapterSong.setMultiSelected(true);
        rcv_songs.setAdapter(adapterSong);
        rcv_songs.setHasFixedSize(true);
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));

        iv_arrow_back = view.findViewById(R.id.iv_arrow);
        iv_confirm = view.findViewById(R.id.iv_confirm);

        final int playlist_pos = getArguments().getInt("playlist_pos");
        iv_confirm.setOnClickListener(v -> ((ActivityMain) getActivity()).addSelectedSongToPlaylist(playlist_pos));
        iv_arrow_back.setOnClickListener(v -> ((ActivityMain)getActivity()).onBackPressed());
        return view;
    }
    @Override
    public void onDestroy() {
        ActivityMain.songs = ((ActivityMain)getActivity()).getMusicProvider().loadSongs();
        musicSrv.setList(ActivityMain.songs);
        super.onDestroy();
    }
}
