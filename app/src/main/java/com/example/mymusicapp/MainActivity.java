package com.example.mymusicapp;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar_main);
        viewPager = findViewById(R.id.view_pager);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new SongsFrag(), "SONGS");
        pagerAdapter.addFragment(new ArtistsFrag(), "ARTISTS");
        pagerAdapter.addFragment(new AlbumsFrag(), "ALBUMS");
        pagerAdapter.addFragment(new PlaylistFrag(), "PLAYLIST");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}