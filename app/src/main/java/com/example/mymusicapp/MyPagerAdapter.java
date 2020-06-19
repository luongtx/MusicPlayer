package com.example.mymusicapp;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

class MyPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    int totalTabs;
    private String[] pageTitles = new String[]{"SONGS", "ARTISTS", "ALBUMS", "PLAYLIST"};
    public MyPagerAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                SongsFrag songsFrag = new SongsFrag();
                return songsFrag;
            case 1:
                ArtistsFrag artistsFrag = new ArtistsFrag();
                return artistsFrag;
            case 2:
                AlbumsFrag albumsFrag = new AlbumsFrag();
                return albumsFrag;
            case 3:
                PlaylistFrag playlistFrag = new PlaylistFrag();
                return playlistFrag;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}