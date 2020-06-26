package com.example.mymusicapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.mymusicapp.entity.Artist;
import com.example.mymusicapp.MusicProvider;
import com.example.mymusicapp.MusicService;
import com.example.mymusicapp.PlaybackController;
import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.adapter.AdapterArtist;
import com.example.mymusicapp.adapter.AdapterMyPager;
import com.example.mymusicapp.adapter.AdapterSong;
import com.example.mymusicapp.fragment.FragmentAlbums;
import com.example.mymusicapp.fragment.FragmentArtistDetail;
import com.example.mymusicapp.fragment.FragmentArtists;
import com.example.mymusicapp.fragment.FragmentMediaControl;
import com.example.mymusicapp.fragment.FragmentPlaylist;
import com.example.mymusicapp.fragment.FragmentSongs;
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
    static MusicProvider musicProvider;
    PlaybackController playbackController;
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

        musicProvider = new MusicProvider(this);
        songs = musicProvider.loadSongs();
        artists = musicProvider.loadArtist();

        playbackController = new PlaybackController(layout_mini_play);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onPlayNewSong() {
        changSongDisplay();
    }

    @Override
    public void onMusicPause() {
        changSongDisplay();
    }

    @Override
    public void onMusicResume() {
        changSongDisplay();
    }

    @Override
    public void onSongItemClick(int position) {
        playbackController.songPicked(position);
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