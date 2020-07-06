package com.example.mymusicapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterSong;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.example.mymusicapp.activity.ActivityMain.songs;
/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSelectSongs extends Fragment {

    public FragmentSelectSongs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_songs, container, false);
        RecyclerView rcv_songs = view.findViewById(R.id.rcv_select_songs);

        songs = ((ActivityMain)context).loadAllSongs();
        AdapterSong adapterSong = new AdapterSong(songs, getContext());
        adapterSong.initSelectedSongs();
        adapterSong.setMultiSelected(true);
        rcv_songs.setAdapter(adapterSong);
        rcv_songs.setHasFixedSize(true);
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));

        ImageView iv_arrow_back = view.findViewById(R.id.iv_arrow);
        ImageView iv_confirm = view.findViewById(R.id.iv_confirm);

        final int playlist_pos = getArguments().getInt("playlist_pos");
        iv_confirm.setOnClickListener(v -> {
            ((ActivityMain)context).addSelectedSongToPlaylist(true, playlist_pos);
        });
        iv_arrow_back.setOnClickListener(v -> ((ActivityMain)context).onBackPressed());
        return view;
    }

    Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
