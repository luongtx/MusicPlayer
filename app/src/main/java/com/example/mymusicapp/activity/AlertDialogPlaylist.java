package com.example.mymusicapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Playlist;

import java.util.HashSet;
import java.util.Set;

import static com.example.mymusicapp.activity.ActivityMain.playLists;

public class AlertDialogPlaylist extends AppCompatActivity {

    Context context;

    public AlertDialogPlaylist(Context context) {
        this.context = context;
    }

    public void createDialogAddSongsToPlaylist() {
        AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(R.string.add_selected_song_to_playlist);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(layout.getContext());
        editText.setHint(R.string.enter_new_playlist);
        editText.setPadding(20, 20, 20, 20);
        layout.addView(editText);
        Set<Integer> set_selected_playlist = new HashSet<>();
        for (Playlist playlist : playLists) {
            TextView textView = new TextView(layout.getContext());
            textView.setTextSize(20);
            textView.setPadding(10, 10, 10, 10);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(playlist.getName());
            layout.addView(textView);
            textView.setOnClickListener(v -> {
                int selectedId = playLists.indexOf(playlist);
                if (set_selected_playlist.contains(selectedId)) {
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
            if (!playlist_name.isEmpty()) {
                //add playlist
                ((ActivityMain) context).addPlaylist(new Playlist(playlist_name));
                playLists = ((ActivityMain) context).getAllPlaylists();
                //add song to new playlist
                ((ActivityMain) context).addSelectedSongToPlaylist(false,playLists.size() - 1);
            } else {
                for (Integer index : set_selected_playlist) {
                    ((ActivityMain) context).addSelectedSongToPlaylist(false, index);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void createDialogAddNewPlaylist() {
        //show input dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.enter_new_playlist);
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlist_name = input.getText().toString().trim();
                if(playlist_name.length() == 0) {
                    Toast.makeText(context, R.string.enter_playlist_name, Toast.LENGTH_SHORT).show();
                } else {
                    //add playlist
                    Playlist playlist = new Playlist();
                    playlist.setName(playlist_name);
                    if (playLists.stream().noneMatch(pls -> pls.getName().equals(playlist_name))) {
                        playLists.add(playlist);
                        ((ActivityMain) context).addPlaylist(playlist);
                        ((ActivityMain) context).fragmentPlaylist.notifyDataChanged();
                    } else {
                        Toast.makeText( context, getString(R.string.playlist_name_duplicate), Toast.LENGTH_SHORT).show();
                    }
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
    }

}
