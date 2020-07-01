package com.example.mymusicapp;

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

import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public static MediaPlayer player;
    private ArrayList<Song> songs;
    public static int currSongIndex;
    static boolean isLooping;
    static boolean isShuffling;
    private final IBinder musicBind = new MusicBinder();

    public interface ServiceCallbacks {
        void onPlayNewSong();
        void onMusicPause();
        void onMusicResume();
    }

    private ServiceCallbacks serviceCallbacks;

    public void setCallBacks(ServiceCallbacks callBacks) {
        serviceCallbacks = callBacks;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currSongIndex = -1;
        isLooping = false;
        isShuffling = false;
        player = new MediaPlayer();
        initMusicPlayer();
    }

//    public class LocalBinder extends Binder {
//        MusicService getService() {
//            return MusicService.this;
//        }
//    }

    public void initMusicPlayer() {

        //The wake lock will let playback continue when the device becomes idle
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
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
        currSongIndex = songIndex;
        if (songs != null && currSongIndex + 1 <= songs.size() && currSongIndex >= 0) {
            if (isShuffling) currSongIndex = generateRandomIdx();
            Song playSong = songs.get(currSongIndex);
            int songId = playSong.getId();
            Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
            serviceCallbacks.onPlayNewSong();
        } else {
            player.pause();
            serviceCallbacks.onMusicPause();
        }
    }

    public void pause() {
        player.pause();
        serviceCallbacks.onMusicPause();
    }

    public void resume() {
        if (currSongIndex >= songs.size()) {
            playSong(songs.size() - 1);
        } else {
            player.start();
        }
        serviceCallbacks.onMusicResume();
    }

    public int getCurrSongIndex() {
        return currSongIndex;
    }

    public int generateRandomIdx() {
        int newIndex = (int) (Math.random() * songs.size());
        while (newIndex == currSongIndex) {
            newIndex = (int) (Math.random() * songs.size());
        }
        return newIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isLooping) {
            playSong(currSongIndex);
        } else {
            if (isShuffling) {
                currSongIndex = generateRandomIdx();
                playSong(currSongIndex);
            } else {
                playSong(currSongIndex + 1);
                serviceCallbacks.onPlayNewSong();
            }
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public static String getHumanTime(int milliseconds) {
        int seconds = milliseconds/1000;
        int minutes = seconds/60;
        int r_seconds = seconds % 60;
        String min_toString = Integer.toString(minutes);
        String sec_toString = Integer.toString(r_seconds);
        if (minutes < 10) min_toString = "0" + min_toString;
        if (r_seconds < 10) sec_toString = "0" + sec_toString;
        return min_toString + ":" + sec_toString;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
}