package com.example.mymusicapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicService.ServiceCallbacks {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    static ArrayList<Song> songs;

    LinearLayout layout_mini_play;
    ImageButton iv_prev, iv_play, iv_next;

    SongsFrag songsFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_main);
        viewPager = findViewById(R.id.pager);

        setSupportActionBar(toolbar);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new SongsFrag(), "SONGS");
        pagerAdapter.addFragment(new ArtistsFrag(), "ARTISTS");
        pagerAdapter.addFragment(new AlbumsFrag(), "ALBUMS");
        pagerAdapter.addFragment(new PlaylistFrag(), "PLAYLIST");


        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        layout_mini_play = findViewById(R.id.layout_mini_play);
        layout_mini_play.setVisibility(View.GONE);
        iv_prev = (ImageButton) findViewById(R.id.iv_prev);
        iv_play = (ImageButton) findViewById(R.id.iv_play);
        iv_next = (ImageButton) findViewById(R.id.iv_next);
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.toggle_play();
            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playSong(musicSrv.getCurrSongIndex() - 1);
                highlightSong();
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playSong(musicSrv.getCurrSongIndex() + 1);
                highlightSong();
            }
        });
    }

    public void highlightSong(){
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
        int currSongIndex = musicSrv.getCurrSongIndex();
        if(viewPager.getCurrentItem() == 0 && page != null) {
            ((SongsFrag)page).changeSongItemDisplay(currSongIndex);
        }
    }

    public void requestReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public ArrayList<Song> loadSongs() {
        requestReadStorage();
        ArrayList<Song> list_songs = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
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
        songs = list_songs;
        return songs;
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songs);
            musicBound = true;
            musicSrv.setCallBacks(MainActivity.this); //register service call back
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void songPicked(int position){
        if(position != musicSrv.getCurrSongIndex()) {
            musicSrv.playSong(position);
        } else {
            musicSrv.toggle_play();
        }
        layout_mini_play.setVisibility(View.VISIBLE);
        iv_play.setPressed(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    @Override
    public void onSongIndexChanged() {
        highlightSong();
    }
}