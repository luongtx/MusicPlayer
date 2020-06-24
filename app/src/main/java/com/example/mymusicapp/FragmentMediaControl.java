package com.example.mymusicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class FragmentMediaControl extends Fragment implements MusicService.ServiceCallbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    ImageView btn_end;
    ImageView iv_dvd;
    SeekBar postionBar, volumnBar;
    TextView tvStart, tvEnd;
    TextView tvArtist, tvTitle;
    ImageButton btn_next, btn_prev, btn_play, btn_shuffle, btn_loop;
    LinearLayout layout_mini_play;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media_control, container, false);
        Song song = ActivityMain.songs.get(MusicService.currSongIndex);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());


        iv_dvd = view.findViewById(R.id.ivDVD);
        btn_end = view.findViewById(R.id.iv_end);
        postionBar = view.findViewById(R.id.positionBar);
        tvStart = view.findViewById(R.id.elapsedTimeLabel);
        tvEnd = view.findViewById(R.id.remainingTimeLabel);
        volumnBar = view.findViewById(R.id.volumeBar);
        btn_next = view.findViewById(R.id.iv_next);
        btn_prev = view.findViewById(R.id.iv_prev);
        btn_play = view.findViewById(R.id.iv_play);
        btn_shuffle = view.findViewById(R.id.iv_shuffle);
        btn_loop = view.findViewById(R.id.iv_loop);

        layout_mini_play = view.findViewById(R.id.layout_mini_play);
        layout_mini_play.setClickable(false);

        Glide.with(view).load(R.drawable.img_dvd_spinning).into(iv_dvd);
        postionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ActivityMain.musicSrv.setSeekPosition(progress);
                postionBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvStart.setText(MusicService.getHumanTime(ActivityMain.musicSrv.getSeekPosition()));
        tvEnd.setText(MusicService.getHumanTime(song.getDuration()));

        volumnBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumnNum = progress/100f;
                ActivityMain.musicSrv.setVolume(volumnNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain)getActivity()).popStackedFragment();
            }
        });
        ActivityMain.musicSrv.setCallBacks(FragmentMediaControl.this);
        return view;
    }

    public void resetView() {
        Song newSong = ActivityMain.songs.get(MusicService.currSongIndex);
        tvTitle.setText(newSong.getTitle());
        tvArtist.setText(newSong.getArtist());
        tvStart.setText("0:00");
        tvEnd.setText(MusicService.getHumanTime(newSong.getDuration()));
    }

    @Override
    public void onPlayNewSong() {
        resetView();
        ((ActivityMain)getActivity()).onPlayNewSong();
    }

    @Override
    public void onMusicPause() {
        Glide.with(getView()).load(R.drawable.img_dvd_pause).into(iv_dvd);
        ((ActivityMain)getActivity()).onMusicPause();
    }

    @Override
    public void onMusicResume() {
        Glide.with(getView()).load(R.drawable.img_dvd_spinning).into(iv_dvd);
        ((ActivityMain)getActivity()).onMusicResume();
    }
}
