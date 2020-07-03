package com.example.mymusicapp.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.mymusicapp.MusicProvider;
import com.example.mymusicapp.MusicService;
import com.example.mymusicapp.PlaybackController;
import com.example.mymusicapp.R;
import com.example.mymusicapp.adapter.AdapterArtist;
import com.example.mymusicapp.adapter.AdapterMyPager;
import com.example.mymusicapp.adapter.AdapterPlayList;
import com.example.mymusicapp.adapter.AdapterSong;
import com.example.mymusicapp.entity.Artist;
import com.example.mymusicapp.entity.Playlist;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.fragment.FragmentArtistDetail;
import com.example.mymusicapp.fragment.FragmentArtists;
import com.example.mymusicapp.fragment.FragmentMediaControl;
import com.example.mymusicapp.fragment.FragmentPlaylist;
import com.example.mymusicapp.fragment.FragmentPlaylistDetails;
import com.example.mymusicapp.fragment.FragmentSelectSongs;
import com.example.mymusicapp.fragment.FragmentSongs;
import com.example.mymusicapp.model.ModelSelectedItem;
import com.example.mymusicapp.repository.DBMusicHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ActivityMain extends AppCompatActivity implements MusicService.ServiceCallbacks,
        AdapterSong.SongItemClickListener, AdapterArtist.ArtistItemClickListener,
        AdapterPlayList.PlaylistClickListener {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    AdapterMyPager pagerAdapter;
    public static MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    public static ArrayList<Song> songs;
    public static ArrayList<Artist> artists;
    public static ArrayList<Playlist> playLists;

    public static ArrayList<ModelSelectedItem> modelSelectedItems;
    LinearLayout layout_mini_play;
    MusicProvider musicProvider;
    DBMusicHelper dbMusicHelper;
    PlaybackController playbackController;

    FragmentSongs fragmentSongs;
    FragmentArtists fragmentArtists;
    FragmentPlaylist fragmentPlaylist;
    FragmentPlaylistDetails fragmentPlaylistDetails;
    FragmentArtistDetail fragmentArtistDetail;
    FragmentSelectSongs fragmentSelectSongs;
    private int[] tabIcons = {R.drawable.ic_audiotrack, R.drawable.ic_star, R.drawable.ic_featured_play_list};
    private int[] tabTitles = {R.string.songs, R.string.artists, R.string.playlists};
    String name, check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_main);
        viewPager = findViewById(R.id.pager);

        setSupportActionBar(toolbar);
        pagerAdapter = new AdapterMyPager(getSupportFragmentManager());
        pagerAdapter.addFragment(new FragmentSongs(), getString(R.string.songs));
        pagerAdapter.addFragment(new FragmentArtists(), getString(R.string.artists));
        pagerAdapter.addFragment(new FragmentPlaylist(), getString(R.string.playlists));
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setIconForTabTitle();
        layout_mini_play = findViewById(R.id.layout_mini_play);
        layout_mini_play.setVisibility(View.GONE);

        musicProvider = new MusicProvider(this);
        songs = musicProvider.loadSongs();
        artists = musicProvider.loadArtist();

        dbMusicHelper = new DBMusicHelper(ActivityMain.this);
        playLists = dbMusicHelper.getAllPlaylists();
        playbackController = new PlaybackController(layout_mini_play);

        fragmentSongs = (FragmentSongs) pagerAdapter.getItem(0);
        fragmentArtists = (FragmentArtists) pagerAdapter.getItem(1);
        fragmentPlaylist = (FragmentPlaylist) pagerAdapter.getItem(2);
        initModelSelectedItems(songs.size());
        name = getIntent().getStringExtra("name");
        check = getIntent().getStringExtra("check");

    }

    private void setIconForTabTitle() {
        for(int i = 0 ; i< tabIcons.length; i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
//            tabLayout.getTabAt(i).setText(tabTitles[i]);
        }
    }

    public ArrayList<ModelSelectedItem> initModelSelectedItems(int size) {
        modelSelectedItems = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            modelSelectedItems.add(new ModelSelectedItem(i, false));
        }
        return modelSelectedItems;
    }

    public void changSongDisplay() {
        fragmentSongs.changeSongItemDisplay(musicSrv.getCurrSongIndex());
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

    private Menu menu;
    MenuItem it_refresh, it_new_playlist, it_sleep_timer, it_add_to_other_playlist,
            it_add_to_this_playlist, it_delete_from_playlist , it_deselect_item, it_my_account;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        it_my_account = menu.findItem(R.id.menu_my_account);
        String title = name.length() == 0 ? getString(R.string.login) : name;
        it_my_account.setTitle(title);
        it_refresh = menu.findItem(R.id.it_refresh);
        it_new_playlist = menu.findItem(R.id.it_new_playlist);
        it_sleep_timer = menu.findItem(R.id.it_sleep_timer);
        it_add_to_other_playlist = menu.findItem(R.id.it_add_to_other_playlist);
        it_add_to_this_playlist = menu.findItem(R.id.it_add_song_to_this_playlist);
        it_delete_from_playlist = menu.findItem(R.id.it_delete_from_playlist);
        it_deselect_item = menu.findItem(R.id.it_deselect_item);
        setOnClickListenerForMenuItem();

        SearchView searchView = (SearchView) searchItem.getActionView();
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
                if (fragmentPlaylistDetails != null) {
                    fragmentPlaylistDetails.getAdapterSong().getFilter().filter(newText);
                }
                if (fragmentArtistDetail != null) {
                    fragmentArtistDetail.getAdapterSong().getFilter().filter(newText);
                }
                return false;
            }
        });
        return true;
    }

    private void setOnClickListenerForMenuItem() {
        it_my_account.setOnMenuItemClickListener( menuItem -> {
            Intent accountIntent = new Intent(ActivityMain.this, ActivityAccount.class);
            accountIntent.putExtra("name", name);
            accountIntent.putExtra("check", check);
            startActivity(accountIntent);
            finish();
            return true;
        });
        it_new_playlist.setOnMenuItemClickListener( menuItem -> {
            onClickAddNewPlaylist();
            return true;
        });
        it_add_to_this_playlist.setOnMenuItemClickListener(menuItem -> {
            onClickOptionAddSongs(fragmentPlaylistDetails.getPlaylist_pos());
            return true;
        });
        it_add_to_other_playlist.setOnMenuItemClickListener(menuItem -> {
            onClickOptionAddToPlaylist();
            return true;
        });
        it_delete_from_playlist.setOnMenuItemClickListener(menuItem -> {
            deleteFromPlaylist();
            return true;
        });
        it_deselect_item.setOnMenuItemClickListener(menuitem -> {
            cancelSelected();
            return true;
        });
    }

    public void changeMenuWhenLongClickItem() {
        it_refresh.setVisible(false);
        it_new_playlist.setVisible(false);
        it_sleep_timer.setVisible(false);
        it_add_to_other_playlist.setVisible(true);
        it_delete_from_playlist.setVisible(true);
        it_deselect_item.setVisible(true);
    }
    public void changeMenuInPlaylistDetails() {
        it_refresh.setVisible(true);
        it_new_playlist.setVisible(true);
        it_sleep_timer.setVisible(true);
        it_add_to_this_playlist.setVisible(true);
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
    public void onSongItemLongClicked() {
        changeMenuWhenLongClickItem();
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

    String playlist_name = "";
    public void onClickAddNewPlaylist() {
        //show input dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_new_playlist);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playlist_name = input.getText().toString();
                if(playlist_name.length() == 0) {
                    Toast.makeText(ActivityMain.this, R.string.enter_playlist_name, Toast.LENGTH_SHORT).show();
                } else {
                    //add playlist
                    Playlist playlist = new Playlist();
                    playlist.setName(playlist_name);
                    playLists.add(playlist);
                    dbMusicHelper.addPlaylist(playlist);
                    fragmentPlaylist.getAdapterPlayList().notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        layout_mini_play.setVisibility(View.GONE);
    }

    public void updatePlaylistName(int position) {
        Playlist playlist = playLists.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_new_playlist);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(50,50,50,50);
        input.setText(playlist.getName());
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playlist_name = input.getText().toString();
                if (playlist_name.length() == 0) {
                    Toast.makeText(ActivityMain.this, R.string.enter_playlist_name, Toast.LENGTH_SHORT).show();
                } else {
                    //rename playlist
                    playlist.setName(playlist_name);
                    dbMusicHelper.updatePlaylist(playlist);
                    fragmentPlaylist.getAdapterPlayList().notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        layout_mini_play.setVisibility(View.GONE);
    }

    @Override
    public void onClickArtistItem(int position) {
        String artistName = artists.get(position).getName();
        fragmentArtistDetail = new FragmentArtistDetail();
        Bundle bundle = new Bundle();
        bundle.putString("artist", artistName);
        fragmentArtistDetail.setArguments(bundle);
        pagerAdapter.get_list_fragment().remove(1);
        pagerAdapter.get_list_fragment().add(1, fragmentArtistDetail);
        pagerAdapter.notifyDataSetChanged();
        setIconForTabTitle();
    }

    @Override
    public void onClickPlaylistItem(int position) {
        changeMenuInPlaylistDetails();
        fragmentPlaylistDetails = new FragmentPlaylistDetails();
        Bundle bundle = new Bundle();
        bundle.putInt("playlist_pos", position);
        fragmentPlaylistDetails.setArguments(bundle);
        pagerAdapter.get_list_fragment().remove(2);
        pagerAdapter.get_list_fragment().add(2, fragmentPlaylistDetails);
        pagerAdapter.notifyDataSetChanged();
        setIconForTabTitle();
    }

    public void deletePlaylist(int position) {
        int playlistId = playLists.get(position).getId();
        playLists.remove(position);
        dbMusicHelper.deletePlaylist(playlistId);
    }

    public void onClickOptionAddToPlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add selected song to playlist");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20,20,20,20);
        EditText editText = new EditText(layout.getContext());
        editText.setHint("Enter new playlist");
        layout.addView(editText);
        Set<Integer> set_selected_playlist = new HashSet<>();
        for(Playlist playlist : playLists) {
            TextView textView = new TextView(layout.getContext());
            textView.setTextSize(20);
            textView.setPadding(10,10,10,10);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(playlist.getName());
            layout.addView(textView);
            textView.setOnClickListener(v -> {
                int selectedId = playLists.indexOf(playlist);
                if(set_selected_playlist.contains(selectedId)){
                    v.setBackgroundColor(Color.WHITE);
                    set_selected_playlist.remove(selectedId);
                } else {
                    v.setBackgroundColor(Color.CYAN);
                    set_selected_playlist.add(selectedId);
                }
            });
        }
        builder.setView(layout);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String playlist_name = editText.getText().toString();
            if(!playlist_name.isEmpty()) {
                //add playlist
                dbMusicHelper.addPlaylist(new Playlist(playlist_name));
                playLists = dbMusicHelper.getAllPlaylists();
                //add song to new playlist
                addSelectedSongToPlaylist(playLists.size()-1);
            } else {
                set_selected_playlist.forEach(index -> addSelectedSongToPlaylist(index));
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void onClickOptionAddSongs(int position) {
        layout_mini_play.setVisibility(View.GONE);
        fragmentSelectSongs = new FragmentSelectSongs();
        Bundle bundle = new Bundle();
        bundle.putInt("playlist_pos", position);
        fragmentSelectSongs.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.layout_main, fragmentSelectSongs);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void addSelectedSongToPlaylist(int position){
        int playlistId = playLists.get(position).getId();
        ArrayList<Song> selectedSongs = new ArrayList<>();
        if (fragmentSelectSongs == null) {
            songs = musicSrv.getSongs();
        }
        for (ModelSelectedItem item : modelSelectedItems) {
            if (item.isSelectd()) {
                selectedSongs.add(songs.get(item.getPosition()));
            }
        }
        dbMusicHelper.addSongsToPlaylist(selectedSongs, playlistId);
        if (fragmentPlaylistDetails != null && position == fragmentPlaylistDetails.getPlaylist_pos()) {
            fragmentPlaylistDetails.getAdapterSong().setList(dbMusicHelper.getPlaylistSongs(playlistId));
        }
        popStackedFragment();
    }

    public void deleteFromPlaylist() {
        ArrayList<Song> selectedSongs = new ArrayList<>();
        Song song;
        for(ModelSelectedItem modelSelectedItem: modelSelectedItems) {
            if(modelSelectedItem.isSelectd()) {
                song = musicSrv.getSongs().get(modelSelectedItem.getPosition());
                selectedSongs.add(song);
            }
        }
        int playlistId = playLists.get(fragmentPlaylistDetails.getPlaylist_pos()).getId();
        dbMusicHelper.deleteSongsFromPlaylist(playlistId, selectedSongs);
        songs = dbMusicHelper.getPlaylistSongs(playlistId);
        fragmentPlaylistDetails.getAdapterSong().setList(songs);
        musicSrv.setList(songs);
    }

    public void cancelSelected() {
        if (viewPager.getCurrentItem() == 0) {
            fragmentSongs.getAdapterSong().setLongClicked(false);
            fragmentSongs.getAdapterSong().setMultiSelected(false);
            fragmentSongs.getAdapterSong().setModel(initModelSelectedItems(songs.size()));
        } else if (viewPager.getCurrentItem() == 1) {
            fragmentArtistDetail.getAdapterSong().setLongClicked(false);
            fragmentArtistDetail.getAdapterSong().setMultiSelected(false);
            fragmentArtistDetail.getAdapterSong().setModel(initModelSelectedItems(songs.size()));
        } else {
            fragmentPlaylistDetails.getAdapterSong().setLongClicked(false);
            fragmentPlaylistDetails.getAdapterSong().setMultiSelected(false);
            fragmentPlaylistDetails.getAdapterSong().setModel(initModelSelectedItems(songs.size()));
        }
    }

    public DBMusicHelper getDbMusicHelper() {
        return dbMusicHelper;
    }

    public MusicProvider getMusicProvider() {
        return musicProvider;
    }
}