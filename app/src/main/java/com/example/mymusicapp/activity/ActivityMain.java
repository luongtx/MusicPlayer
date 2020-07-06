package com.example.mymusicapp.activity;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.mymusicapp.MusicProvider;
import com.example.mymusicapp.MusicService;
import com.example.mymusicapp.PlaybackController;
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
import com.example.mymusicapp.fragment.FragmentTimerPicker;
import com.example.mymusicapp.repository.DBMusicHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityMain extends AppCompatActivity implements MusicService.ServiceCallbacks,
       TimePickerDialog.OnTimeSetListener {

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

    public static LinearLayout layout_mini_controller;
    public static MusicProvider musicProvider;
    public static DBMusicHelper dbMusicHelper;

    FragmentSongs fragmentSongs;
    FragmentArtists fragmentArtists;
    FragmentPlaylist fragmentPlaylist;
    FragmentPlaylistSongs fragmentPlaylistSongs;
    FragmentArtistSongs fragmentArtistSongs;
    FragmentMediaControl fragmentMediaControl;
    FragmentTimerPicker fragmentTimerPicker;

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
        playbackController = new PlaybackController(layout_mini_controller);

        loadData();
        setOnChangeListenerForViewPager();

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
                if(position == 0) {
                    songs = musicProvider.loadSongs();
                    fragmentSongs.getAdapterSong().setList(songs);
                    fragmentSongs.getAdapterSong().initSelectedSongs();
                    setList(songs);
                } else if(position == 1) {
                    if()
                }
//                recoverFragment();
                recoverMenu();
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
//            tabLayout.getTabAt(i).setText(tabTitles[i]);
        }
    }

    public void highlightCurrentPosition() {
        fragmentSongs.notifySongStateChanges(songs);
        if (fragmentPlaylistSongs != null) fragmentPlaylistSongs.notifySongStateChanges(songs);
        else if (fragmentArtistSongs != null) fragmentArtistSongs.notifySongStateChanges(songs);
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicSrv = binder.getService();
            musicSrv.setList(songs);
            musicBound = true;
            musicSrv.setCallBacks(ActivityMain.this);
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

    private Menu menu;
    MenuItem it_refresh, it_new_playlist, it_sleep_timer, it_add_to_other_playlist,
            it_add_to_this_playlist, it_delete_from_playlist, it_deselect_item, it_my_account,
            it_language, it_vn, it_eng, it_search;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        findMenuItem();
        setOnClickListenerForMenuItem();
        SearchView searchView = (SearchView) it_search.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(getString(R.string.search_song_name));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                fragmentSongs.getAdapterSong().getFilter().filter(newText);
                if (fragmentPlaylistSongs != null) {
                    fragmentPlaylistSongs.getAdapterSong().getFilter().filter(newText);
                }
                if (fragmentArtistSongs != null) {
                    fragmentArtistSongs.getAdapterSong().getFilter().filter(newText);
                }
                return false;
            }
        });
        return true;
    }

    public void findMenuItem() {
        it_search = menu.findItem(R.id.menu_search);
        it_my_account = menu.findItem(R.id.menu_my_account);
        it_refresh = menu.findItem(R.id.it_refresh);
        it_new_playlist = menu.findItem(R.id.it_new_playlist);
        it_sleep_timer = menu.findItem(R.id.it_sleep_timer);
        it_add_to_other_playlist = menu.findItem(R.id.it_add_to_other_playlist);
        it_add_to_this_playlist = menu.findItem(R.id.it_add_song_to_this_playlist);
        it_delete_from_playlist = menu.findItem(R.id.it_delete_from_playlist);
        it_deselect_item = menu.findItem(R.id.it_deselect_item);

        it_language = menu.findItem(R.id.it_change_language);
        it_eng = menu.findItem(R.id.it_eng);
        it_vn = menu.findItem(R.id.it_vn);

        if(name == null || name.length() == 0) it_my_account.setTitle(R.string.login);
        else it_my_account.setTitle(name);
    }

    private void setOnClickListenerForMenuItem() {
        it_my_account.setOnMenuItemClickListener( menuItem -> {
            Intent accountIntent = new Intent(ActivityMain.this, ActivityAccount.class);
            accountIntent.putExtra(ActivityLogin.NAME, name);
            accountIntent.putExtra(ActivityLogin.CHECK, check);
            startActivity(accountIntent);
            finish();
            return true;
        });
        it_new_playlist.setOnMenuItemClickListener( menuItem -> {
            fragmentPlaylist.onClickAddNewPlaylist();
            return true;
        });
        it_add_to_this_playlist.setOnMenuItemClickListener(menuItem -> {
            fragmentPlaylist.onClickOptionAddSongs(fragmentPlaylistSongs.getPlaylist_pos());
            return true;
        });
        it_add_to_other_playlist.setOnMenuItemClickListener(menuItem -> {
            onClickOptionAddToPlaylist();
            return true;
        });
        it_delete_from_playlist.setOnMenuItemClickListener(menuItem -> {
            fragmentPlaylistSongs.deleteFromPlaylist();
            return true;
        });
        it_deselect_item.setOnMenuItemClickListener(menuItem -> {
            cancelSelected();
            return true;
        });

        it_eng.setOnMenuItemClickListener(menuItem -> {
            setLocale("en");
            shaEditor = sharedPreferences.edit();
            shaEditor.putString("prefer_lang", "en");
            shaEditor.apply();
            return true;
        });
        it_vn.setOnMenuItemClickListener(menuItem -> {
            setLocale("vi");
            shaEditor = sharedPreferences.edit();
            shaEditor.putString("prefer_lang", "vi");
            shaEditor.apply();
            return true;
        });

        it_refresh.setOnMenuItemClickListener(menuItem -> {
            refresh();
            return true;
        });

        it_sleep_timer.setOnMenuItemClickListener(menuItem -> {
            fragmentTimerPicker = new FragmentTimerPicker();
            fragmentTimerPicker.show(getSupportFragmentManager(), "Timer picker");
            return true;
        });
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

    public void changeMenuWhenSelectMultipleItem() {
        it_refresh.setVisible(false);
        it_new_playlist.setVisible(false);
        it_sleep_timer.setVisible(false);
        it_add_to_other_playlist.setVisible(true);
        if(viewPager.getCurrentItem() == 2) {
            it_delete_from_playlist.setVisible(true);
        }else {
            it_delete_from_playlist.setVisible(false);
        }
        it_deselect_item.setVisible(true);
    }

    public void changeMenuInPlaylistDetails() {
        it_refresh.setVisible(true);
        it_new_playlist.setVisible(true);
        it_sleep_timer.setVisible(true);
        if (viewPager.getCurrentItem() == 2) {
            it_add_to_this_playlist.setVisible(true);
        } else {
            it_add_to_this_playlist.setVisible(false);
        }
        it_delete_from_playlist.setVisible(false);
        it_deselect_item.setVisible(false);
    }

    public void recoverMenu() {
        it_refresh.setVisible(true);
        it_new_playlist.setVisible(true);
        it_sleep_timer.setVisible(true);
        it_add_to_this_playlist.setVisible(false);
        it_add_to_other_playlist.setVisible(false);
        it_delete_from_playlist.setVisible(false);
        it_deselect_item.setVisible(false);
    }

    @Override
    public void onPlayNewSong() {
        highlightCurrentPosition();
    }

    @Override
    public void onMusicPause() {
        highlightCurrentPosition();
    }

    @Override
    public void onMusicResume() {
        highlightCurrentPosition();
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
//        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            appBarLayout.setVisibility(View.VISIBLE);
            layout_mini_controller.setVisibility(View.VISIBLE);
        } else {
            recoverFragment();
            recoverMenu();
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

    //add song to any playlist
    public void onClickOptionAddToPlaylist() {
        new AlertDialogPlaylist(this);
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
        recoverMenu();
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

    CountDownTimer countDownTimer;
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int milliseconds = (hourOfDay * 3600 + minute * 60) * 1000;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(milliseconds, 1000) {
            long r_hour, r_min, r_sec, secs;
            String s_hour, s_min, s_sec;

            public void onTick(long millisUntilFinished) {
                secs = millisUntilFinished / 1000;
                r_hour = secs / 3600;
                r_min = (secs % 3600) / 60;
                r_sec = secs % 60;
                s_hour = String.valueOf(r_hour);
                s_min = String.valueOf(r_min);
                s_sec = String.valueOf(r_sec);
                if (r_hour < 10) s_hour = "0" + r_hour;
                if (r_min < 10) s_min = "0" + r_min;
                if (r_sec < 10) s_sec = "0" + r_sec;
                it_sleep_timer.setTitle(String.format("%s: %s:%s:%s", getString(R.string.timer), s_hour, s_min, s_sec));
            }
            public void onFinish() {
                it_sleep_timer.setTitle(R.string.set_timer);
                playbackController.stop();
            }
        };
        countDownTimer.start();
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
//
//    public void addSongsToPlaylist(ArrayList<Song> songs, int playlistId) {
//        dbMusicHelper.addSongsToPlaylist(songs, playlistId);
//    }

    public void onClickOptionAddSongs(int playlist_pos) {
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
            setPlaylistSongs(dbMusicHelper.getPlaylistSongs(playlistId));
            popStackedFragment();
            setList(songs);
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

    public FragmentArtistSongs newAristSongFragment() {
        fragmentArtistSongs = new FragmentArtistSongs();
        return fragmentArtistSongs;
    }

    public void setPlaylistSongs(ArrayList<Song> playlistSongs) {
        if(fragmentPlaylistSongs != null) {
            fragmentPlaylistSongs.setPlaylistSongs(playlistSongs);
        }
    }

    public static void setList(ArrayList<Song> songs) {
        ActivityMain.songs = songs;
        musicSrv.setList(songs);
    }


}