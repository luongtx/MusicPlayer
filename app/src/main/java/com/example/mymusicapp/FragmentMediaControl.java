package com.example.mymusicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class FragmentMediaControl extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    ImageView iv_end;
    ImageView iv_dvd;
    SeekBar postionBar, volumnBar;
    TextView tvStart, tvEnd;
    TextView tvArtist, tvTitle;
//    ImageButton btn_shuffle, btn_prev, btn_play, btn_next, btn_loop;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media_control, container, false);
        Bundle bundle = getArguments();
        String song_title = bundle.getString("title");
        String artist = bundle.getString("artist");
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTitle.setText(song_title);
        tvArtist.setText(artist);

        iv_dvd = view.findViewById(R.id.ivDVD);
        iv_end = view.findViewById(R.id.iv_end);
//        btn_shuffle = view.findViewById(R.id.iv_shuffle);
//        btn_prev = view.findViewById(R.id.iv_prev);
//        btn_play = view.findViewById(R.id.iv_play);
//        btn_next = view.findViewById(R.id.iv_next);
//        btn_loop = view.findViewById(R.id.iv_loop);
        postionBar = view.findViewById(R.id.positionBar);
        tvStart = view.findViewById(R.id.elapsedTimeLabel);
        tvEnd = view.findViewById(R.id.remainingTimeLabel);
        volumnBar = view.findViewById(R.id.volumeBar);

        Glide.with(view).load(R.drawable.img_dvd_spinning).into(iv_dvd);
        postionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MusicService.player.seekTo(progress);
                postionBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvStart.setText(bundle.getString("t_start"));
        tvEnd.setText(bundle.getString("t_end"));

        volumnBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumnNum = progress/100f;
                MusicService.player.setVolume(volumnNum, volumnNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        btn_play.setPressed(true);
//        btn_play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityMain.musicSrv.toggle_play();
//                resetView();
//            }
//        });
//
//        btn_prev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityMain.musicSrv.playSong(ActivityMain.musicSrv.getCurrSongIndex() - 1);
//                resetView();
//            }
//        });
//        btn_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityMain.musicSrv.playSong(ActivityMain.musicSrv.getCurrSongIndex() + 1);
//                resetView();
//            }
//        });

        iv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain)getActivity()).popStackedFragment();
            }
        });

        return view;
    }

    public void resetView() {
        Song newSong = ActivityMain.songs.get(ActivityMain.musicSrv.getCurrSongIndex());
        tvTitle.setText(newSong.getTitle());
        tvArtist.setText(newSong.getArtist());
        tvStart.setText("0:00");
        tvEnd.setText(MusicService.getHumanTime(MusicService.player.getDuration()));
//        btn_play.setPressed(true);
    }

}
