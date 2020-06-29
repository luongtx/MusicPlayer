package com.example.mymusicapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterSong;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.example.mymusicapp.activity.ActivityMain.musicSrv;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSelectedSongs extends Fragment {

    private RecyclerView rcv_songs;
    private AdapterSong adapterSong;
    private ImageView iv_arrow_back;
    private TextView tv_num_selected, tv_confirm;
    public FragmentSelectedSongs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_songs, container, false);
        rcv_songs = view.findViewById(R.id.rcv_songs);
        adapterSong = new AdapterSong(ActivityMain.songs);
        adapterSong.setModel(ActivityMain.modelSelectedItems);
        adapterSong.setMultiSelected(true);
        rcv_songs.setAdapter(adapterSong);
        rcv_songs.setHasFixedSize(true);
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));

        iv_arrow_back = view.findViewById(R.id.iv_arrow);
        tv_num_selected = view.findViewById(R.id.tv_num_selected);
        tv_confirm = view.findViewById(R.id.tv_confirm);

        final int playlist_pos = getArguments().getInt("playlist_pos");
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).addSelectedSongToPlaylist(playlist_pos);
            }
        });
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
