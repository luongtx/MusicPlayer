package com.example.mymusicapp.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.MusicService;
import com.example.mymusicapp.PlaybackController;
import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Song;

import java.util.Objects;
import static com.example.mymusicapp.activity.ActivityMain.musicSrv;

public class FragmentMediaControl extends Fragment implements MusicService.ServiceCallbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    ImageView btn_end;
    ImageView iv_dvd;
    SeekBar postionBar, volumnBar;
    TextView tvStart, tvEnd;
    TextView tvArtist, tvTitle;
//    ImageButton btn_next, btn_prev, btn_play, btn_shuffle, btn_loop;
    LinearLayout layout_mini_play;
    Song currentSong;
    SeekBarTask seekBarTask;
    PlaybackController playbackController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_media_control, container, false);

        if (MusicService.currSongIndex <= ActivityMain.songs.size()) {
            currentSong = ActivityMain.songs.get(MusicService.currSongIndex);
        }
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


        layout_mini_play = view.findViewById(R.id.layout_mini_play);
        playbackController = new PlaybackController(layout_mini_play);

        Glide.with(view).load(R.drawable.img_dvd_spinning).into(iv_dvd);

        tvStart.setText(MusicService.getHumanTime(MusicService.player.getCurrentPosition()));
        tvEnd.setText(MusicService.getHumanTime(currentSong.getDuration()));

        volumnBar.setProgress(50);
        volumnBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumnNum = progress / 100f;
                MusicService.player.setVolume(volumnNum, volumnNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_end.setOnClickListener(v -> ((ActivityMain)getActivity()).onBackPressed());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicSrv.setCallBacks(FragmentMediaControl.this);
        seekBarTask = new SeekBarTask();
        seekBarTask.execute();
    }

    private void resetView() {
        if (MusicService.currSongIndex < ActivityMain.songs.size()) {
            currentSong = ActivityMain.songs.get(MusicService.currSongIndex);
        }
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
    }

    @Override
    public void onMusicPause() {
        Glide.with(Objects.requireNonNull(getView())).load(R.drawable.img_dvd_video).into(iv_dvd);
//        ((ActivityMain)getActivity()).onMusicPause();
    }

    @Override
    public void onMusicResume() {
        Glide.with(Objects.requireNonNull(getView())).load(R.drawable.img_dvd_spinning).into(iv_dvd);
//        ((ActivityMain)getActivity()).onMusicResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(seekBarTask != null && isRemoving()) {
            seekBarTask.cancel(false);
        }
    }

    @Override
    public void onDestroy() {
        musicSrv.setCallBacks((ActivityMain)getActivity());
        super.onDestroy();
    }

    class SeekBarTask extends AsyncTask<String, Integer, String> {
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

        //bug: never used
        @Override
        protected void onPostExecute(String s) {
            cancel(true);
            resetView();
        }
    }

}
