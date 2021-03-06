package com.example.mymusicapp.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mymusicapp.service.MusicService;
import com.example.mymusicapp.R;
import com.example.mymusicapp.controller.ActivityMain;
import com.example.mymusicapp.controller.MediaPlaybackController;
import com.example.mymusicapp.entity.Song;

import java.util.Objects;

import static com.example.mymusicapp.controller.ActivityMain.musicSrv;

public class FragmentMediaControl extends Fragment implements MusicService.ServiceCallbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    ImageView btn_end;
    ImageView iv_dvd;
    SeekBar postionBar, volumnBar;
    TextView tvStart, tvEnd;
    TextView tvArtist, tvTitle;
    LinearLayout layout_mini_controller;
    Song currentSong;
    SeekBarTask seekBarTask;
    MediaPlaybackController mediaPlaybackController;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view != null) return view;
        view = inflater.inflate(R.layout.fragment_media_control, container, false);

        currentSong = musicSrv.getSongs().get(MusicService.currentSongPos);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());

        mediaPlaybackController = new MediaPlaybackController(getContext(), view.findViewById(R.id.layout_mini_controller));
        musicSrv.getServiceCallbacks().set(0, mediaPlaybackController);

        iv_dvd = view.findViewById(R.id.ivDVD);
        btn_end = view.findViewById(R.id.iv_end);
        postionBar = view.findViewById(R.id.positionBar);
        tvStart = view.findViewById(R.id.elapsedTimeLabel);
        tvEnd = view.findViewById(R.id.remainingTimeLabel);
        volumnBar = view.findViewById(R.id.volumeBar);

        Glide.with(view).load(R.drawable.img_dvd_spinning).into(iv_dvd);

        tvStart.setText(MusicService.getReadableTime(MusicService.player.getCurrentPosition()));
        tvEnd.setText(MusicService.getReadableTime(currentSong.getDuration()));

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
        musicSrv.addCallBacks(FragmentMediaControl.this);
        seekBarTask = new SeekBarTask();
        seekBarTask.execute();
    }

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void resetView() {
        currentSong = musicSrv.getSongs().get(MusicService.currentSongPos);
        tvTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());
        tvStart.setText("0:00");
        tvEnd.setText(MusicService.getReadableTime(currentSong.getDuration()));

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
    }

    @Override
    public void onMusicResume() {
        Glide.with(Objects.requireNonNull(getView())).load(R.drawable.img_dvd_spinning).into(iv_dvd);
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
        ((ActivityMain) context).resetCallBacks();
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
            tvStart.setText(MusicService.getReadableTime(values[0]));
        }

        //bug: never used
        @Override
        protected void onPostExecute(String s) {
            cancel(true);
            resetView();
        }
    }

}
