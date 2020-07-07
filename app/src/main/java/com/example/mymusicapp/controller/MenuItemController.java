package com.example.mymusicapp.controller;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.SearchView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.fragment.FragmentTimerPicker;

public class MenuItemController {

    private Context context;
    private Menu menu;
    MenuItem it_refresh, it_new_playlist, it_sleep_timer, it_add_to_other_playlist,
            it_add_to_this_playlist, it_delete_from_playlist, it_deselect_item, it_my_account,
            it_language, it_vn, it_eng, it_search;

    private ActivityMain parent;

    MenuItemController(Context context, Menu menu) {
        this.context = context;
        this.menu = menu;
        parent = (ActivityMain) context;
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

        if(parent.getName() == null || parent.getName().length() == 0) it_my_account.setTitle(context.getString(R.string.login));
        else it_my_account.setTitle(parent.getName());
    }

    public void setOnClickListenerForMenuItem() {
        it_my_account.setOnMenuItemClickListener( menuItem -> {
            Intent accountIntent = new Intent(context, ActivityAccount.class);
            accountIntent.putExtra(ActivityLogin.NAME, parent.getName());
            accountIntent.putExtra(ActivityLogin.CHECK, parent.getCheck());
            parent.startActivity(accountIntent);
            parent.finish();
            return true;
        });
        it_new_playlist.setOnMenuItemClickListener( menuItem -> {
            parent.createDialogAddNewPlaylist();
            return true;
        });
        it_add_to_this_playlist.setOnMenuItemClickListener(menuItem -> {
            parent.onClickOptionAddSongs(-1);
            return true;
        });
        it_add_to_other_playlist.setOnMenuItemClickListener(menuItem -> {
            parent.onClickOptionAddToPlaylist();
            return true;
        });
        it_delete_from_playlist.setOnMenuItemClickListener(menuItem -> {
            parent.deleteFromPlaylist();
            return true;
        });
        it_deselect_item.setOnMenuItemClickListener(menuItem -> {
            parent.cancelSelected();
            return true;
        });

        it_eng.setOnMenuItemClickListener(menuItem -> {
            parent.setLocale("en");
            parent.savePreferLang("en");
            return true;
        });
        it_vn.setOnMenuItemClickListener(menuItem -> {
            parent.setLocale("vi");
            parent.savePreferLang("vi");
            return true;
        });

        it_refresh.setOnMenuItemClickListener(menuItem -> {
            parent.refresh();
            return true;
        });

        it_sleep_timer.setOnMenuItemClickListener(menuItem -> {
            FragmentTimerPicker fragmentTimerPicker = new FragmentTimerPicker(parent);
            fragmentTimerPicker.show(parent.getSupportFragmentManager(), "Timer picker");
            return true;
        });
    }

    public void changeMenuWhenSelectMultipleItem() {
        it_refresh.setVisible(false);
        it_new_playlist.setVisible(false);
        it_sleep_timer.setVisible(false);
        it_add_to_other_playlist.setVisible(true);
        if(parent.getCurrentPagePosition() == 2) {
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
        if (parent.getCurrentPagePosition() == 2) {
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

    public void createSearchView() {
        SearchView searchView = (SearchView) it_search.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(context.getString(R.string.search_song_name));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                parent.setFilterForFragment(newText);
                return false;
            }
        });
    }

    static CountDownTimer countDownTimer;
    public void startCounterClock(int hourOfDay, int minute) {

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
                it_sleep_timer.setTitle(String.format("%s: %s:%s:%s", parent.getString(R.string.timer), s_hour, s_min, s_sec));
            }
            public void onFinish() {
                it_sleep_timer.setTitle(R.string.set_timer);
                parent.stopPlayBack();
            }
        };
        countDownTimer.start();
    }
}
