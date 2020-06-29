package com.example.mymusicapp.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.adapter.AdapterSong;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.example.mymusicapp.activity.ActivityMain.musicSrv;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSongs extends Fragment {

    private RecyclerView rcv_songs;
    private AdapterSong adapterSong;
    private static int lastClickPosition = -1;
    public FragmentSongs() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        rcv_songs = view.findViewById(R.id.rcv_songs);
        adapterSong = new AdapterSong(ActivityMain.songs);
        adapterSong.setMultiSelected(false);
        rcv_songs.setAdapter(adapterSong);
        rcv_songs.setHasFixedSize(true);
        rcv_songs.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_songs.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));
        return view;
    }

    public void changeSongItemDisplay(int position) {
        View view = rcv_songs.getLayoutManager().findViewByPosition(position);
        if(view !=null ) {
            ImageView imageView = view.findViewById(R.id.ivSong);
            if (lastClickPosition == position) {
                if (imageView.isActivated()) {
                    imageView.setActivated(false);
                    Glide.with(view).load(R.drawable.img_dvd).into(imageView);
                } else {
                    imageView.setActivated(true);
                    Glide.with(view).load(R.drawable.img_dvd_playing).into(imageView);
                }
            } else {
                view.setBackgroundColor(Color.CYAN);
                Glide.with(view).load(R.drawable.img_dvd_playing).into(imageView);
                imageView.setActivated(true);
                View oldView = rcv_songs.getLayoutManager().findViewByPosition(lastClickPosition);
                if (oldView != null) {
                    oldView.setBackgroundColor(Color.WHITE);
                    ImageView oldImg = oldView.findViewById(R.id.ivSong);
                    Glide.with(view).load(R.drawable.img_dvd).into(oldImg);
                }
            }
        }
        lastClickPosition = position;
    }

    public AdapterSong getAdapterSong() {
        return adapterSong;
    }

    @Override
    public void onDestroy() {
        ActivityMain.songs = ((ActivityMain)getActivity()).getMusicProvider().loadSongs();
        musicSrv.setList(ActivityMain.songs);
        musicSrv.setCallBacks((ActivityMain)getActivity());
        super.onDestroy();
    }
}