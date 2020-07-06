package com.example.mymusicapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusicapp.R;
import com.example.mymusicapp.activity.ActivityMain;
import com.example.mymusicapp.adapter.AdapterPlayList;
import com.example.mymusicapp.entity.Playlist;
import com.example.mymusicapp.entity.Song;

import java.util.ArrayList;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.example.mymusicapp.activity.ActivityMain.dbMusicHelper;
import static com.example.mymusicapp.activity.ActivityMain.playLists;
import static com.example.mymusicapp.activity.ActivityMain.songs;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlaylist extends Fragment implements AdapterPlayList.PlaylistClickListener{

    private AdapterPlayList adapterPlayList;
    public FragmentPlaylist() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        RecyclerView rcv_playlists = view.findViewById(R.id.rcv_playlist);
        adapterPlayList = new AdapterPlayList(context, playLists);
        adapterPlayList.setPlaylistClickListener(this);
        rcv_playlists.setAdapter(adapterPlayList);
        rcv_playlists.setHasFixedSize(true);
        rcv_playlists.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcv_playlists.addItemDecoration(new DividerItemDecoration(view.getContext(), HORIZONTAL));
        return view;
    }

    public AdapterPlayList getAdapterPlayList() {
        return adapterPlayList;
    }

    public void updatePlaylistName(int position) {
        Playlist playlist = playLists.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.enter_new_playlist);
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(50, 50, 50, 50);
        input.setText(playlist.getName());
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlist_name = input.getText().toString();
                if (playlist_name.length() == 0) {
                    Toast.makeText(getContext(), R.string.enter_playlist_name, Toast.LENGTH_SHORT).show();
                } else {
                    //rename playlist
                    playlist.setName(playlist_name);
                    ((ActivityMain) context).updatePlaylist(playlist);
                    getAdapterPlayList().notifyDataSetChanged();
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

    @Override
    public void onClickPlaylistItem(int position) {
        ((ActivityMain) context).changeMenuInPlaylistDetails();
        FragmentPlaylistSongs fragmentPlaylistSongs = ((ActivityMain) context).newPlaylistSongFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("playlist_pos", position);
        fragmentPlaylistSongs.setArguments(bundle);
        ((ActivityMain) context).replace_fragment(false, 2, fragmentPlaylistSongs);
        ((ActivityMain) context).setIconForTabTitle();
    }

    public void onClickOptionAddSongs(int position) {
        ActivityMain.layout_mini_controller.setVisibility(View.GONE);
        FragmentSelectSongs fragmentSelectSongs = new FragmentSelectSongs();
        Bundle bundle = new Bundle();
        bundle.putInt("playlist_pos", position);
        fragmentSelectSongs.setArguments(bundle);
        ((ActivityMain)context).replace_fragment(true, 0, fragmentSelectSongs);
    }

    public void deletePlaylist(int position) {
        int playlistId = playLists.get(position).getId();
        playLists.remove(position);
        ((ActivityMain) context).deletePlaylist(playlistId);
    }

    public void addSelectedSongToPlaylist(int playlistId) {
        ArrayList<Song> selectedSongs = new ArrayList<>();
        for (Song song : songs) {
            if (song.isSelected()) {
                selectedSongs.add(song);
            }
        }
        dbMusicHelper.addSongsToPlaylist(selectedSongs, playlistId);
    }

    Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void notifyDataChanged() {
        adapterPlayList.notifyDataSetChanged();
    }
}
