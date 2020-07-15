package com.example.mymusicapp.controller;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.example.mymusicapp.R;
import com.example.mymusicapp.service.MusicService;

import static com.example.mymusicapp.controller.ActivityMain.musicSrv;
import static com.example.mymusicapp.controller.ActivityMain.songs;

public class MediaPlaybackController implements View.OnClickListener, MusicService.ServiceCallbacks{


    Context context;
    View view;
    private int currSongId = -1;
    ImageButton btn_prev, btn_play, btn_next, btn_shuffle, btn_loop;

    public MediaPlaybackController(Context context, View view) {
        this.context = context;
        this.view = view;
        btn_prev = view.findViewById(R.id.iv_prev);
        btn_prev.setOnClickListener(this);
        btn_play = view.findViewById(R.id.iv_play);
        btn_play.setOnClickListener(this);
        btn_next = view.findViewById(R.id.iv_next);
        btn_next.setOnClickListener(this);
        btn_shuffle = view.findViewById(R.id.iv_shuffle);
        btn_shuffle.setOnClickListener(this);
        btn_loop = view.findViewById(R.id.iv_loop);
        btn_loop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_prev:
                previous();
                break;
            case R.id.iv_next:
                next();
                break;
            case R.id.iv_play:
                toggle_play();
                break;
            case R.id.iv_shuffle:
                shuffle();
            case R.id.iv_loop:
                loop();
        }
    }

    public void previous() {
        MusicService.isTouching = false;
        musicSrv.playSong(musicSrv.getPlayingSongPos() - 1);
        btn_play.setBackgroundResource(R.drawable.ic_pause);
    }

    public void toggle_play() {
        if (MusicService.player.isPlaying()) {
            musicSrv.pause();
            btn_play.setBackgroundResource(R.drawable.ic_play);
        } else {
            musicSrv.resume();
            btn_play.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    public void next() {
        MusicService.isTouching = false;
        musicSrv.playSong(musicSrv.getPlayingSongPos() + 1);
        btn_play.setBackgroundResource(R.drawable.ic_pause);
    }

    public void shuffle() {
        btn_shuffle = view.findViewById(R.id.iv_shuffle);
        if (MusicService.isShuffling) {
            MusicService.isShuffling = false;
            btn_shuffle.setImageResource(R.drawable.ic_shuffle);
        } else {
            MusicService.isShuffling = true;
            btn_shuffle.setImageResource(R.drawable.ic_shuffle_active);
        }
    }

    void loop() {
        btn_loop = view.findViewById(R.id.iv_loop);
        if (MusicService.isLooping) {
            MusicService.isLooping = false;
            btn_loop.setImageResource(R.drawable.ic_loop_black);
        } else {
            MusicService.isLooping = true;
            btn_loop.setImageResource(R.drawable.ic_loop_active);
        }
    }

    public void songPicked(int position) {
        musicSrv.setList(songs);
        MusicService.isTouching = true;
        MusicService.currentPagePos = ((ActivityMain) context).getCurrentPagePosition();
        int pickedId = musicSrv.getSongIdAt(position);
        if (pickedId != currSongId) {
            musicSrv.playSong(position);
            btn_play.setBackgroundResource(R.drawable.ic_pause);
        } else {
            toggle_play();
        }
        currSongId = pickedId;
        view.setVisibility(View.VISIBLE);
    }

    public void stop() {
        musicSrv.pause();
        btn_play.setBackgroundResource(R.drawable.ic_play);
    }

    @Override
    public void onPlayNewSong() {
        ((ActivityMain) context).highlightCurrentPosition();
        btn_play.setBackgroundResource(R.drawable.ic_pause);
        MusicService.isTouching = false;
    }

    @Override
    public void onMusicPause() {
        ((ActivityMain) context).highlightCurrentPosition();
        btn_play.setBackgroundResource(R.drawable.ic_play);
    }

    @Override
    public void onMusicResume() {
        ((ActivityMain) context).highlightCurrentPosition();
        btn_play.setBackgroundResource(R.drawable.ic_pause);
    }
}


