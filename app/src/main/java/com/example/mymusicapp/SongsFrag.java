package com.example.mymusicapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFrag extends Fragment {

    private View song_view;
    ArrayList<Song> songs;
    ListView lv_songs;

    public SongsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        song_view  = inflater.inflate(R.layout.fragment_songs, container, false);
        return song_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv_songs = view.findViewById(R.id.lv_songs);
        songs = getSongList();
        SongAdapter songAdapter = new SongAdapter(view.getContext(), R.layout.song_item, songs);
        lv_songs.setAdapter(songAdapter);
    }

    private void requestReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (song_view.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private ArrayList<Song> getSongList() {
        requestReadStorage();
        ArrayList<Song> list_songs = new ArrayList<>();
        ContentResolver musicResolver = song_view.getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM};
        Cursor musicCursor = musicResolver.query(musicUri, columns, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            do {
                int id = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                list_songs.add(new Song(id,title,artist,album));

            } while(musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        return list_songs;
    }
}
