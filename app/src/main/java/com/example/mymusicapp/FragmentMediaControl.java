package com.example.mymusicapp;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    Song currentSong;
    SeekBarTask seekBarTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media_control, container, false);
        currentSong = ActivityMain.songs.get(MusicService.currSongIndex);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());

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
                if(fromUser) {
                    MusicService.player.seekTo(progress);
                    postionBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvStart.setText(MusicService.getHumanTime(ActivityMain.musicSrv.getSeekPosition()));
        tvEnd.setText(MusicService.getHumanTime(currentSong.getDuration()));

        volumnBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumnNum = progress/100f;
                MusicService.player.setVolume(volumnNum,volumnNum);
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
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActivityMain.musicSrv.setCallBacks(FragmentMediaControl.this);
        if(seekBarTask == null) {
            seekBarTask = new SeekBarTask();
            seekBarTask.execute();
        }
    }

    public void resetView() {
        currentSong = ActivityMain.songs.get(MusicService.currSongIndex);
        tvTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());
        tvStart.setText("0:00");
        tvEnd.setText(MusicService.getHumanTime(currentSong.getDuration()));
        seekBarTask = new SeekBarTask();
        seekBarTask.execute();
    }

    @Override
    public void onPlayNewSong() {
        resetView();
//        ((ActivityMain)getActivity()).onPlayNewSong();
    }

    @Override
    public void onMusicPause() {
        Glide.with(getView()).load(R.drawable.img_dvd_video).into(iv_dvd);
        btn_play.setBackgroundResource(R.drawable.ic_play);
//        ((ActivityMain)getActivity()).onMusicPause();
    }

    @Override
    public void onMusicResume() {
        Glide.with(getView()).load(R.drawable.img_dvd_spinning).into(iv_dvd);
        btn_play.setBackgroundResource(R.drawable.ic_pause);
//        ((ActivityMain)getActivity()).onMusicResume();
    }

    private class SeekBarTask extends AsyncTask<String, Integer, String> {
        int duration;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            duration = currentSong.getDuration();
            //bug: never completed
            while(MusicService.player.getCurrentPosition() < duration){
                try {
                    publishProgress(MusicService.player.getCurrentPosition());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return "done";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            postionBar.setProgress(100*values[0]/duration);
            tvStart.setText(MusicService.getHumanTime(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            cancel(true);
            resetView();
        }

    }
}
