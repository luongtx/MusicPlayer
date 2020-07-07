package com.example.mymusicapp.controller;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.mymusicapp.service.MusicProvider;
import com.example.mymusicapp.service.MusicService;
import com.example.mymusicapp.R;
import com.example.mymusicapp.adapter.AdapterViewPager;
import com.example.mymusicapp.entity.Artist;
import com.example.mymusicapp.entity.Playlist;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.fragment.FragmentArtistSongs;
import com.example.mymusicapp.fragment.FragmentArtists;
import com.example.mymusicapp.fragment.FragmentMediaControl;
import com.example.mymusicapp.fragment.FragmentPlaylist;
import com.example.mymusicapp.fragment.FragmentPlaylistSongs;
import com.example.mymusicapp.fragment.FragmentSongs;
import com.example.mymusicapp.repository.DBMusicHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityMain extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    AppBarLayout appBarLayout;
    AdapterViewPager pagerAdapter;
    public static MusicService musicSrv;
    PlaybackController playbackController;
    private Intent playIntent;
    private boolean musicBound = false;
    public static ArrayList<Song> all_songs;
    public static ArrayList<Song> songs;
    public static ArrayList<Artist> artists;
    public static ArrayList<Playlist> playLists;

    public LinearLayout layout_mini_controller;
    public static MusicProvider musicProvider;
    public static DBMusicHelper dbMusicHelper;

    FragmentSongs fragmentSongs;
    FragmentArtists fragmentArtists;
    FragmentPlaylist fragmentPlaylist;
    FragmentPlaylistSongs fragmentPlaylistSongs;
    FragmentArtistSongs fragmentArtistSongs;
    FragmentMediaControl fragmentMediaControl;
    DialogController dialogController;
    MenuItemController menuItemController;
    private int[] tabIcons = {R.drawable.ic_audiotrack, R.drawable.ic_star, R.drawable.ic_featured_play_list};
    private int[] tabTitles = {R.string.songs, R.string.artists, R.string.playlists};
    String name, check;
    public Locale myLocale;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor shaEditor;
    String prefer_lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appBarLayout = findViewById(R.id.layout_appbar);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_main);
        viewPager = findViewById(R.id.pager);

        setSupportActionBar(toolbar);

        setUpViewPager();
        setIconForTabTitle();

        fragmentSongs = (FragmentSongs) pagerAdapter.getItem(0);
        fragmentArtists = (FragmentArtists) pagerAdapter.getItem(1);
        fragmentPlaylist = (FragmentPlaylist) pagerAdapter.getItem(2);

        layout_mini_controller = findViewById(R.id.layout_mini_controller);
        layout_mini_controller.setVisibility(View.GONE);
        playbackController = new PlaybackController(this, layout_mini_controller);

        loadData();
        setOnChangeListenerForViewPager();
        dialogController = new DialogController(this);
    }

    private void loadData() {
        musicProvider = new MusicProvider(this);
        all_songs = musicProvider.loadSongs();
        songs = all_songs;
        artists = musicProvider.loadArtist();
        dbMusicHelper = new DBMusicHelper(ActivityMain.this);
        playLists = dbMusicHelper.getAllPlaylists();

        sharedPreferences = getSharedPreferences(ActivityLogin.MY_PREFS_FILENAME, MODE_PRIVATE);
        name = sharedPreferences.getString(ActivityLogin.NAME, "");
        check = sharedPreferences.getString(ActivityLogin.CHECK, "");
        prefer_lang = sharedPreferences.getString("prefer_lang", "en");

        setLocale(prefer_lang);
    }

    private void setUpViewPager() {
        pagerAdapter = new AdapterViewPager(getSupportFragmentManager());
        pagerAdapter.addFragment(new FragmentSongs(), getString(R.string.songs));
        pagerAdapter.addFragment(new FragmentArtists(), getString(R.string.artists));
        pagerAdapter.addFragment(new FragmentPlaylist(), getString(R.string.playlists));
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setOnChangeListenerForViewPager() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    songs = musicProvider.loadSongs();
                    if (MusicService.currentPagePos == position)
                        musicSrv.indexCurrentSong(songs);
                    fragmentSongs.notifySongStateChanges(songs);
                } else if (position == 1) {
                    if (pagerAdapter.getItem(1).equals(fragmentArtistSongs)) {
                        songs = musicProvider.loadSongsByArtist(fragmentArtistSongs.getArtistName());
                        if (MusicService.currentPagePos == position)
                            musicSrv.indexCurrentSong(songs);
                        fragmentArtistSongs.notifySongStateChanges(songs);
                    }
                } else {
                    if (pagerAdapter.getItem(2).equals(fragmentPlaylistSongs)) {
                        songs = dbMusicHelper.getPlaylistSongs(fragmentPlaylistSongs.getPlaylist_pos());
                        if (MusicService.currentPagePos == position)
                            musicSrv.indexCurrentSong(songs);
                        fragmentPlaylistSongs.notifySongStateChanges(songs);
                    }
                }
                menuItemController.recoverMenu();
                setIconForTabTitle();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setIconForTabTitle() {
        for(int i = 0 ; i< tabIcons.length; i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicSrv = binder.getService();
            musicSrv.setList(songs);
            musicBound = true;
            musicSrv.setCallBacks(playbackController);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void highlightCurrentPosition() {
        if (getCurrentPagePosition() == 0) {
            fragmentSongs.notifySongStateChanges(songs);
        } else if (getCurrentPagePosition() == 1) {
            fragmentArtistSongs.notifySongStateChanges(songs);
        } else {
            fragmentPlaylistSongs.notifySongStateChanges(songs);
        }
    }

    public String getName() {
        return name;
    }

    public String getCheck() {
        return check;
    }

    private Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItemController = new MenuItemController(this, menu);
        menuItemController.findMenuItem();
        menuItemController.setOnClickListenerForMenuItem();
        menuItemController.createSearchView();
        return true;
    }

    public void setFilterForFragment(String newText) {
        if (viewPager.getCurrentItem() == 0) {
            fragmentSongs.getAdapterSong().getFilter().filter(newText);
        } else if (viewPager.getCurrentItem() == 1) {
            fragmentArtistSongs.getAdapterSong().getFilter().filter(newText);
        } else {
            fragmentPlaylistSongs.getAdapterSong().getFilter().filter(newText);
        }
    }

    public void refresh() {
        all_songs = musicProvider.loadSongs();
        songs = all_songs;
        artists = musicProvider.loadArtist();
        playLists = dbMusicHelper.getAllPlaylists();
        fragmentSongs.getAdapterSong().setList(songs);
        if (fragmentArtists != null) {
            fragmentArtists.getAdapterArtist().setList(artists);
        }
        if (fragmentPlaylist != null) {
            fragmentPlaylist.getAdapterPlayList().setList(playLists);
        }
    }

    public void maximizeMediaController(View view) {
        fragmentMediaControl = new FragmentMediaControl();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_main, fragmentMediaControl);
        transaction.addToBackStack(null);
        transaction.commit();
        appBarLayout.setVisibility(View.GONE);
        layout_mini_controller.setVisibility(View.GONE);
    }

    public void popStackedFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            appBarLayout.setVisibility(View.VISIBLE);
            layout_mini_controller.setVisibility(View.VISIBLE);
        } else {
            recoverFragment();
            menuItemController.recoverMenu();
            setIconForTabTitle();
        }
    }

    public void recoverFragment() {
        if(viewPager.getCurrentItem() == 1) {
            pagerAdapter.get_list_fragment().remove(1);
            pagerAdapter.get_list_fragment().add(1, fragmentArtists);
            pagerAdapter.notifyDataSetChanged();
        } else if(viewPager.getCurrentItem() == 2) {
            pagerAdapter.get_list_fragment().remove(2);
            pagerAdapter.get_list_fragment().add(2, fragmentPlaylist);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    public void onClickOptionAddToPlaylist() {
        createDialogAddToPlaylist();
    }

    public void createDialogAddToPlaylist() {
        dialogController.createDialogAddSongsToPlaylist();
    }

    public void cancelSelected() {
        if (viewPager.getCurrentItem() == 0) {
            fragmentSongs.getAdapterSong().setMultiSelected(false);
            fragmentSongs.getAdapterSong().initSelectedSongs();
        } else if (viewPager.getCurrentItem() == 1) {
            fragmentArtistSongs.getAdapterSong().setMultiSelected(false);
            fragmentArtistSongs.getAdapterSong().initSelectedSongs();
        } else {
            fragmentPlaylistSongs.getAdapterSong().setMultiSelected(false);
            fragmentPlaylistSongs.getAdapterSong().initSelectedSongs();
        }
        menuItemController.recoverMenu();
    }

    public void savePreferLang(String lang) {
        shaEditor = sharedPreferences.edit();
        shaEditor.putString("prefer_lang", lang);
        shaEditor.apply();
    }

    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        if (!config.locale.getLanguage().equals(myLocale.getLanguage())) {
            config.locale = myLocale;
            res.updateConfiguration(config, dm);
            Intent refresh = new Intent(this, ActivityMain.class);
            refresh.putExtras(bundle());
            startActivity(refresh);
        }
    }

    public Bundle bundle() {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("check", check);
        return bundle;
    }

    public void stopPlayBack() {
        playbackController.stop();
    }

    public void replace_fragment(boolean isTransaction, int pos, Fragment fragment) {
        if (isTransaction) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layout_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            pagerAdapter.get_list_fragment().remove(pos);
            pagerAdapter.get_list_fragment().add(pos, fragment);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    public void onClickOptionAddSongs(int playlist_pos) {
        if (playlist_pos == -1)
            fragmentPlaylist.onClickOptionAddSongs(fragmentPlaylistSongs.getPlaylist_pos());
        else
            fragmentPlaylist.onClickOptionAddSongs(playlist_pos);
    }

    public ArrayList<Song> getPlaylistSongs(int playlistId) {
        return dbMusicHelper.getPlaylistSongs(playlistId);
    }

    public void deleteSongsFromPlaylist(int playlistId, ArrayList<Song> selectedSongs) {
        dbMusicHelper.deleteSongsFromPlaylist(playlistId, selectedSongs);
    }

    public ArrayList<Song> loadSongsByArtist(String artistName) {
        return musicProvider.loadSongsByArtist(artistName);
    }

    public ArrayList<Artist> getArtists() {
        return musicProvider.loadArtist();
    }

    public void deletePlaylist(int playlistId) {
        dbMusicHelper.deletePlaylist(playlistId);
    }

    public void addPlaylist(Playlist playlist) {
        dbMusicHelper.addPlaylist(playlist);
    }

    public ArrayList<Playlist> getAllPlaylists() {
        return dbMusicHelper.getAllPlaylists();
    }

    public void addSelectedSongToPlaylist(boolean isCurrentPlaylist, int pos) {
        int playlistId = playLists.get(pos).getId();
        fragmentPlaylist.addSelectedSongToPlaylist(playlistId);
        if (isCurrentPlaylist) {
            songs = dbMusicHelper.getPlaylistSongs(playlistId);
            setPlaylistSongs(songs);
            popStackedFragment();
        }
    }

    public void updatePlaylist(Playlist playlist) {
        dbMusicHelper.updatePlaylist(playlist);
    }

    public ArrayList<Song> loadAllSongs() {
        return musicProvider.loadSongs();
    }

    public int getCurrentPagePosition() {
        return viewPager.getCurrentItem();
    }

    public void pickSong(int position) {
        playbackController.songPicked(position);
    }

    public void deleteFromPlaylist() {
        fragmentPlaylistSongs.deleteFromPlaylist();
    }

    public FragmentPlaylistSongs newPlaylistSongFragment() {
        fragmentPlaylistSongs = new FragmentPlaylistSongs();
        return fragmentPlaylistSongs;
    }

    public FragmentArtistSongs newArtistSongFragment() {
        fragmentArtistSongs = new FragmentArtistSongs();
        return fragmentArtistSongs;
    }

    public void setPlaylistSongs(ArrayList<Song> playlistSongs) {
        if(fragmentPlaylistSongs != null) {
            fragmentPlaylistSongs.setPlaylistSongs(playlistSongs);
        }
    }

//    public static void setList(ArrayList<Song> songs) {
//        ActivityMain.songs = songs;
//        if(musicSrv != null) {
//            musicSrv.setList(songs);
//        }
//    }

    public void changeMenuWhenSelectMultipleItem() {
        menuItemController.changeMenuWhenSelectMultipleItem();
    }

    public void changeMenuInPlaylistDetails() {
        menuItemController.changeMenuInPlaylistDetails();
    }

    public void createDialogAddNewPlaylist() {
        dialogController.createDialogAddNewPlaylist();
    }

    public void hideMiniController() {
        layout_mini_controller.setVisibility(View.GONE);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        menuItemController.startCounterClock(hourOfDay, minute);
    }

    public void resetCallBacks() {
        musicSrv.setCallBacks(playbackController);
    }

}