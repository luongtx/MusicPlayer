package com.example.mymusicapp;

import android.view.View;
import android.widget.ImageButton;

import static com.example.mymusicapp.activity.ActivityMain.musicSrv;

public class PlaybackController {


    View view;
    ImageButton iv_prev, iv_play, iv_next, iv_shuffle, iv_loop;
    static int prev_song_id = -1, curr_song_id = -1;
    public PlaybackController(View view) {
        this.view = view;
        iv_prev = (ImageButton) view.findViewById(R.id.iv_prev);
        iv_play = (ImageButton) view.findViewById(R.id.iv_play);
        iv_next = (ImageButton) view.findViewById(R.id.iv_next);
        iv_loop = (ImageButton) view.findViewById(R.id.iv_loop);
        iv_shuffle = (ImageButton) view.findViewById(R.id.iv_shuffle);

        iv_prev.setOnClickListener(this::previous);

        iv_play.setOnClickListener(this::toggle_play);

        iv_next.setOnClickListener(this::next);

        iv_shuffle.setOnClickListener(this::shuffle);

        iv_loop.setOnClickListener(this::loop);
    }

    void previous(View view) {
        musicSrv.playSong(musicSrv.getCurrSongIndex() - 1);
    }

    void toggle_play(View view) {
        if (MusicService.player.isPlaying()) {
            musicSrv.pause();
            iv_play.setBackgroundResource(R.drawable.ic_play);
        } else {
            musicSrv.resume();
            iv_play.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    void next(View view) {
        musicSrv.playSong(musicSrv.getCurrSongIndex() + 1);
    }

    void shuffle(View view) {
        iv_shuffle = view.findViewById(R.id.iv_shuffle);
        if (MusicService.isShuffling) {
            MusicService.isShuffling = false;
            iv_shuffle.setImageResource(R.drawable.ic_shuffle);
        } else {
            MusicService.isShuffling = true;
            iv_shuffle.setImageResource(R.drawable.ic_shuffle_active);
        }
    }

    void loop(View view) {
        iv_loop = view.findViewById(R.id.iv_loop);
        if (MusicService.isLooping) {
            MusicService.isLooping = false;
            iv_loop.setImageResource(R.drawable.ic_loop_black);
        } else {
            MusicService.isLooping = true;
            iv_loop.setImageResource(R.drawable.ic_loop_active);
        }
    }

    public void songPicked(int position) {
        curr_song_id = musicSrv.getSongs().get(position).getId();
        if (curr_song_id != prev_song_id) {
            musicSrv.playSong(position);
        } else {
            toggle_play(view);
        }
        prev_song_id = curr_song_id;
        view.setVisibility(View.VISIBLE);
    }
}
