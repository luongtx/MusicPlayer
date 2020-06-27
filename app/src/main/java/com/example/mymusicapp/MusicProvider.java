package com.example.mymusicapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.example.mymusicapp.entity.Artist;
import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

public class MusicProvider {

    Activity activity;

    public MusicProvider(Activity activity) {
        this.activity = activity;
    }

    public void requestReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public ArrayList<Song> loadSongs() {
        requestReadStorage();
        ArrayList<Song> list_songs = new ArrayList<>();
        ContentResolver musicResolver = activity.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                int id = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int duration = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                list_songs.add(new Song(id, title, artist, album, duration));

            } while (musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        return list_songs;
    }

    public ArrayList<Song> loadSongsByArtist(String artistName) {
        requestReadStorage();
        ArrayList<Song> list_songs = new ArrayList<>();
        ContentResolver musicResolver = activity.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Artists.ARTIST + "=?";
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, new String[]{artistName}, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                int id = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int duration = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                list_songs.add(new Song(id, title, artist, album, duration));

            } while (musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        return list_songs;
    }

    public ArrayList<Artist> loadArtist() {
        requestReadStorage();
        ArrayList<Artist> list_artist = new ArrayList<>();
        ContentResolver musicResolver = activity.getContentResolver();
        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_TRACKS};
        Cursor musicCursor = musicResolver.query(artistUri, columns, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            do {
                int id = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Artists._ID));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                int numberOfSongs = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                list_artist.add(new Artist(id, artist, numberOfSongs));
            } while (musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        return list_artist;
    }
}
