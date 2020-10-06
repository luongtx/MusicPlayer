package com.example.mymusicapp.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public static MediaPlayer player;
    private ArrayList<Song> songs;
    public static int currentSongPos;
    public static boolean isLooping;
    public static boolean isShuffling;
    public static boolean isTouching;
    public static int currentPagePos;
    private final IBinder musicBind = new MusicBinder();
    public static final int STATE_PAUSE = 0;
    public static final int STATE_RESUME = 1;
    public static final int STATE_NEW = 2;

    public interface ServiceCallbacks {
        void onPlayNewSong();

        void onMusicPause();

        void onMusicResume();
    }

    private ArrayList<ServiceCallbacks> serviceCallbacks;

    public void addCallBacks(ServiceCallbacks callback) {
        serviceCallbacks.add(callback);
    }

    public void cancelCallBack() {
        serviceCallbacks.clear();
    }

    public void callBack(int state) {
        if (state == 0) {
            serviceCallbacks.forEach(ServiceCallbacks::onMusicPause);
        } else if (state == 1) {
            serviceCallbacks.forEach(ServiceCallbacks::onMusicResume);
        } else {
            serviceCallbacks.forEach(ServiceCallbacks::onPlayNewSong);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentSongPos = -1;
        currentPagePos = -1;
        isLooping = false;
        isShuffling = false;
        player = new MediaPlayer();
        initMusicPlayer();
        serviceCallbacks = new ArrayList<>();
    }

    public void initMusicPlayer() {
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
        if (songs == null || songIndex > songs.size() - 1 || songIndex < 0) {
            pause();
        } else {
            player.reset();
            resetSongState();
            currentSongPos = songIndex;
            if (isShuffling && !isTouching) currentSongPos = generateRandomIndex();
            Song playSong = songs.get(currentSongPos);
            int songId = playSong.getId();
            Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            player.prepareAsync();
            playSong.setState(1);
            callBack(STATE_NEW);
        }
    }

    public void togglePlay() {
        if (currentSongPos == -1) return;
        if (player.isPlaying()) {
            pause();
        } else {
            resume();
        }
    }

    public void resetSongState() {
        songs.forEach(song -> song.setState(-1));
    }

    public void pause() {
        player.pause();
        songs.get(currentSongPos).setState(0);
        callBack(STATE_PAUSE);
    }

    public void resume() {
        if (currentSongPos >= songs.size()) {
            playSong(songs.size() - 1);
        } else {
            player.start();
            songs.get(currentSongPos).setState(1);
        }
        callBack(STATE_RESUME);
    }

    public int getPlayingSongPos() {
        return currentSongPos;
    }

    public int generateRandomIndex() {
        int newIndex = (int) (Math.random() * songs.size());
        while (newIndex == currentSongPos) {
            newIndex = (int) (Math.random() * songs.size());
        }
        return newIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isLooping) {
            playSong(currentSongPos);
        } else {
            if (isShuffling) {
                currentSongPos = generateRandomIndex();
                playSong(currentSongPos);
            } else {
                playSong(currentSongPos + 1);
                callBack(STATE_NEW);
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

    public static String getReadableTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
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

    public int getSongIdAt(int position) {
        return songs.get(position).getId();
    }

    public int getCurrentSongId() {
        return songs.get(currentSongPos).getId();
    }

    public static boolean isIsShuffling() {
        return isShuffling;
    }

    public static boolean isIsLooping() {
        return isLooping;
    }

    public void indexCurrentSong(ArrayList<Song> songs) {
        if (player != null && currentSongPos != -1) {
            if (player.isPlaying()) {
                songs.get(currentSongPos).setState(1);
            } else {
                songs.get(currentSongPos).setState(0);
            }
        }
    }

    public Song getCurrentSong() {
        if (currentSongPos < 0) return songs.get(0);
        else if (currentSongPos > songs.size() - 1) return songs.get(songs.size() - 1);
        else return songs.get(currentSongPos);
    }
}