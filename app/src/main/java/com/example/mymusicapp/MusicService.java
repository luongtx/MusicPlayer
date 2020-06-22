package com.example.mymusicapp;

import java.util.ArrayList;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int currSongIndex;
    private final IBinder musicBind = new MusicBinder();

    public interface ServiceCallbacks {
        void onSongIndexChanged();
    }

    private ServiceCallbacks SongItemChange;
    @Override
    public void onCreate() {
        super.onCreate();
        currSongIndex = -1;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void initMusicPlayer() {

        //The wake lock will let playback continue when the device becomes idle
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void setCallBacks(ServiceCallbacks callBacks){
        SongItemChange = callBacks;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void playSong(int songIndex) {
        player.reset();
        if(songIndex + 1 <= MainActivity.songs.size() && songIndex >= 0) {
            currSongIndex = songIndex;
            Song playSong = songs.get(currSongIndex);
            int currSong = playSong.getId();
            Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
        }
    }

    public void toggle_play() {
        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
    }

    public int getCurrSongIndex() {
        return currSongIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playSong(currSongIndex + 1);
        SongItemChange.onSongIndexChanged();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

}