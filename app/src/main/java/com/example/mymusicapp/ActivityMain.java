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
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity implements MusicService.ServiceCallbacks,
        AdapterSong.SongItemClickListeneer, AdapterArtist.ArtistItemClickListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    static MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    static ArrayList<Song> songs;
    static ArrayList<Artist> artists;
    LinearLayout layout_mini_play;
    ImageButton iv_prev, iv_play, iv_next, iv_loop, iv_shuffle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_main);
        viewPager = findViewById(R.id.pager);

        setSupportActionBar(toolbar);
        AdapterMyPager pagerAdapter = new AdapterMyPager(getSupportFragmentManager());
        pagerAdapter.addFragment(new FragmentSongs(), "SONGS");
        pagerAdapter.addFragment(new FragmentArtists(), "ARTISTS");
        pagerAdapter.addFragment(new FragmentAlbums(), "ALBUMS");
        pagerAdapter.addFragment(new FragmentPlaylist(), "PLAYLIST");


        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        layout_mini_play = findViewById(R.id.layout_mini_play);
        layout_mini_play.setVisibility(View.GONE);
        iv_prev = (ImageButton) findViewById(R.id.iv_prev);
        iv_play = (ImageButton) findViewById(R.id.iv_play);
        iv_next = (ImageButton) findViewById(R.id.iv_next);
        iv_loop = (ImageButton) findViewById(R.id.iv_loop);
        iv_shuffle = (ImageButton) findViewById(R.id.iv_shuffle);

    }

    public void previous(View view) {
        musicSrv.playSong(musicSrv.getCurrSongIndex() - 1);
    }

    public void toggle_play(View view) {
        iv_play = view.findViewById(R.id.iv_play);
        musicSrv.toggle_play();
    }

    public void next(View view) {
        musicSrv.playSong(musicSrv.getCurrSongIndex() + 1);
    }

    public void shuffle(View view) {
        iv_shuffle = view.findViewById(R.id.iv_shuffle);
        if (MusicService.isShuffling) {
            MusicService.isShuffling = false;
            iv_shuffle.setImageResource(R.drawable.ic_shuffle);
        } else {
            MusicService.isShuffling = true;
            iv_shuffle.setImageResource(R.drawable.ic_shuffle_active);
        }
    }

    public void loop(View view) {
        iv_loop = view.findViewById(R.id.iv_loop);
        if (MusicService.isLooping) {
            MusicService.isLooping = false;
            iv_loop.setImageResource(R.drawable.ic_loop_black);
        } else {
            MusicService.isLooping = true;
            iv_loop.setImageResource(R.drawable.ic_loop_active);
        }
    }

    public void changSongDisplay() {
        FragmentSongs.changeSongItemDisplay(musicSrv.getCurrSongIndex());
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
//        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM};
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            do {
                int id = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int duration = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                list_songs.add(new Song(id,title,artist,album,duration));

            } while(musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        songs = list_songs;
        return songs;
    }

    public ArrayList<Song> loadSongsByArtist(String artistName) {
        requestReadStorage();
        ArrayList<Song> list_songs = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String[] columns = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM};
        String selection = MediaStore.Audio.Artists.ARTIST + "=?";
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, new String[]{artistName}, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            do {
                int id = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int duration = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                list_songs.add(new Song(id,title,artist,album,duration));

            } while(musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        songs = list_songs;
        return songs;
    }

    public ArrayList<Artist> loadArtist() {
        requestReadStorage();
        ArrayList<Artist> list_artist = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] columns = {MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_TRACKS};
        Cursor musicCursor = musicResolver.query(artistUri, columns, null, null, null);
        if(musicCursor != null && musicCursor.moveToFirst()){
            do {
                int id = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Artists._ID));
                String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                int numberOfSongs = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                list_artist.add(new Artist(id,artist,numberOfSongs));
            } while(musicCursor.moveToNext());
        }
        assert musicCursor != null;
        musicCursor.close();
        artists = list_artist;
        return artists;
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
            musicSrv.setCallBacks(ActivityMain.this); //register service call back
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
        if (position != musicSrv.getCurrSongIndex()) {
            musicSrv.playSong(position);
        } else {
            musicSrv.toggle_play();
        }
        layout_mini_play.setVisibility(View.VISIBLE);
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
    public void onPlayNewSong() {
        changSongDisplay();
        iv_play.setPressed(true);
    }

    @Override
    public void onMusicPause() {
        iv_play.setBackgroundResource(R.drawable.ic_play);
//        Glide.with(getCurrentFocus()).load(R.drawable.ic_play).into(iv_play);
        changSongDisplay();
    }

    @Override
    public void onMusicResume() {
        iv_play.setBackgroundResource(R.drawable.ic_pause);
//        Glide.with(getCurrentFocus()).load(R.drawable.ic_pause).into(iv_play);
        changSongDisplay();
    }

    @Override
    public void onSongItemClick(int position) {
        songPicked(position);
    }


    @Override
    public void onClickArtistItem(int position) {
        String artistName = artists.get(position).getName();
        FragmentArtistDetail fragmentArtistDetail = new FragmentArtistDetail();
        Bundle bundle = new Bundle();
        bundle.putString("artist", artistName);
        fragmentArtistDetail.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_main, fragmentArtistDetail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void maximizeMediaControl(View view) {
        FragmentMediaControl fragmentMediaControl = new FragmentMediaControl();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_main, fragmentMediaControl);
        transaction.addToBackStack(null);
        transaction.commit();
        layout_mini_play.setVisibility(View.GONE);
    }

    public void popStackedFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            layout_mini_play.setVisibility(View.VISIBLE);
            musicSrv.setCallBacks(ActivityMain.this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }
}