package com.example.mymusicapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSongs extends Fragment {

    static RecyclerView rcv_songs;

    static int lastClickPosition = -1;
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
        setRetainInstance(true);
        return view;
    }

    public static void changeSongItemDisplay(int position) {
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
//    public static void changeSongItemDisplay(int position) {
//        View view = rcv_songs.getLayoutManager().findViewByPosition(position);
//        if (view != null) {
//            view.setBackgroundColor(Color.CYAN);
//            ImageView imageView = view.findViewById(R.id.ivSong);
//            Glide.with(view).load(R.drawable.img_dvd_playing).into(imageView);
//            view = rcv_songs.getLayoutManager().findViewByPosition(lastClickPosition);
////            if(imageView.isPressed()) imageView.setPressed(false);
////            else imageView.setPressed(true);
//            if (view != null) {
//                imageView = view.findViewById(R.id.ivSong);
//                if (lastClickPosition != position) {
//                    view.setBackgroundColor(Color.WHITE);
//                    imageView.setImageResource(R.drawable.img_dvd);
//                } else {
//                    if (imageView.isPressed()) {
//                        imageView.setPressed(false);
//                        imageView.setImageResource(R.drawable.img_dvd);
//                    } else {
//                        imageView.setPressed(true);
//                        Glide.with(view).load(R.drawable.img_dvd_playing).into(imageView);
//                    }
//                }
//            }
//            lastClickPosition = position;
//        }
//    }

//    public static void changeSongItemDisplay(int position) {
//        AdapterSong.SongViewHolder songViewHolder = (AdapterSong.SongViewHolder) rcv_songs.findViewHolderForAdapterPosition(position);
//        if (songViewHolder.view != null) {
//            Glide.with(songViewHolder.view).load(R.drawable.img_dvd_playing).into(songViewHolder.ivImg);
//            songViewHolder.view.setBackgroundColor(Color.CYAN);
//            if (lastClickPosition != position) {
//                songViewHolder = (AdapterSong.SongViewHolder) rcv_songs.findViewHolderForAdapterPosition(lastClickPosition);
//                songViewHolder.view.setBackgroundColor(Color.WHITE);
//                songViewHolder.ivImg.setImageResource(R.drawable.img_dvd);
//            }
//            lastClickPosition = position;
//        }
//    }
}
