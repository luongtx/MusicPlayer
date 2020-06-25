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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
    static MusicProvider musicProvider;
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

        musicProvider = new MusicProvider(this);
        songs = musicProvider.loadSongs();
        artists = musicProvider.loadArtist();
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
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
        int currSongIndex = musicSrv.getCurrSongIndex();
        if (viewPager.getCurrentItem() == 0 && page != null) {
            ((FragmentSongs) page).changeSongItemDisplay(currSongIndex);
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicSrv = binder.getService();
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

    @Override
    public void onPlayNewSong() {
        changSongDisplay();
        iv_play.setPressed(true);
    }

    @Override
    public void onMusicPause() {
        iv_play.setBackgroundResource(R.drawable.ic_play);
        changSongDisplay();
    }

    @Override
    public void onMusicResume() {
        iv_play.setBackgroundResource(R.drawable.ic_pause);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        layout_mini_play.setVisibility(View.VISIBLE);
        musicSrv.setCallBacks(ActivityMain.this);
    }
}