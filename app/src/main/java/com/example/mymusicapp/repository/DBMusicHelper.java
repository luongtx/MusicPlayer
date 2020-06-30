package com.example.mymusicapp.repository;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mymusicapp.MusicProvider;
import com.example.mymusicapp.R;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.entity.Playlist;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DBMusicHelper extends SQLiteOpenHelper {

    private Context context;
    SQLiteDatabase db;
    MusicProvider musicProvider;
    ArrayList<Song> all_songs;
    public DBMusicHelper(Context context) {
        super(context, DBMusicSchema.DB_NAME, null, 3);
        this.context = context;
        musicProvider = new MusicProvider((Activity) context);
        all_songs = musicProvider.loadSongs();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE " + DBMusicSchema.TablePlaylist.TABLE_NAME + " ("
                + DBMusicSchema.TablePlaylist.COL_ID + " integer primary key autoincrement" + ","
                + DBMusicSchema.TablePlaylist.COL_NAME + " TEXT"
                + ");";
        db.execSQL(sql1);
        String sql2 = "CREATE TABLE " + DBMusicSchema.TablePlaylistSong.TABLE_NAME + " ("
                + DBMusicSchema.TablePlaylistSong.COL_ID + " integer primary key autoincrement" + ","
                + DBMusicSchema.TablePlaylistSong.COL_SONG_ID + " INTEGER" + ","
                + DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + " INTEGER"
                + ");";
        db.execSQL(sql2);
        Toast.makeText(context, "Create Database successfully", Toast.LENGTH_SHORT).show();
        Log.d("DB CREATION", "db created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ DBMusicSchema.TablePlaylist.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DBMusicSchema.TablePlaylistSong.TABLE_NAME);
        onCreate(db);
    }

    public void addPlaylist(Playlist playList) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBMusicSchema.TablePlaylist.COL_NAME, playList.getName());
        db.insert(DBMusicSchema.TablePlaylist.TABLE_NAME, null, values);
        Toast.makeText(context, R.string.success_add_playlist, Toast.LENGTH_SHORT).show();
    }
//
//    public void addSongToPlaylist(Song song) {
//        db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(DBMusicSchema.TablePlaylistSong.COL_SONG_ID, song.getId());
//        values.put(DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID, song.getPlaylist_id());
//        db.insert(DBMusicSchema.TablePlaylistSong.TABLE_NAME, null, values);
//    }

    public void addSongsToPlaylist(ArrayList<Song> songs, int playlistId) {
        db = this.getWritableDatabase();
        ContentValues values;
        for(Song song: songs) {
            values = new ContentValues();
            values.put(DBMusicSchema.TablePlaylistSong.COL_SONG_ID, song.getId());
            values.put(DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID, playlistId);
            db.insert(DBMusicSchema.TablePlaylistSong.TABLE_NAME, null, values);
        }
        Toast.makeText(context, R.string.success_add_songs, Toast.LENGTH_SHORT).show();
    }

    public void deletePlaylist(int playlistId) {
        db = this.getWritableDatabase();
        //delete playlist
        String where_cls = DBMusicSchema.TablePlaylist.COL_ID + "=?";
        String[] where_args = new String[] {String.valueOf(playlistId)};
        db.delete(DBMusicSchema.TablePlaylist.TABLE_NAME, where_cls, where_args);
        //delete all song from deleted playlist
        where_cls = DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + "=?";
        where_args = new String[] {String.valueOf(playlistId)};
        db.delete(DBMusicSchema.TablePlaylistSong.TABLE_NAME, where_cls, where_args);
        Toast.makeText(context, R.string.success_delete_playlist, Toast.LENGTH_SHORT).show();
    }

//    public void deleteSongFromPlaylist(int playlistId, int songId) {
//        db = this.getWritableDatabase();
//        String where_cls = DBMusicSchema.TablePlaylistSong.COL_SONG_ID + "=?" + " AND " + DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + "=?";
//        String[] where_args = new String[]{String.valueOf(playlistId), String.valueOf(songId)};
//        db.delete(DBMusicSchema.TablePlaylistSong.TABLE_NAME, where_cls, where_args);
//    }

    public void deleteSongsFromPlaylist(int playlistId, ArrayList<Song> songs) {
        db = this.getWritableDatabase();
        String where_cls = DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + "=" + playlistId + " AND " + DBMusicSchema.TablePlaylistSong.COL_SONG_ID + "=?";
        String[] where_args;
        for (Song song : songs) {
            where_args = new String[]{String.valueOf(song.getId())};
            db.delete(DBMusicSchema.TablePlaylistSong.TABLE_NAME, where_cls, where_args);
        }
        Toast.makeText(context, R.string.seleted_playlist_song_deleted, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Playlist> getAllPlaylists () {
        ArrayList<Playlist> playlists = new ArrayList<>();
        db = this.getWritableDatabase();
        String query = "SELECT * FROM "+ DBMusicSchema.TablePlaylist.TABLE_NAME;
        Cursor queryCursor = db.rawQuery(query, null);
        Playlist playlist;
        if(queryCursor.moveToFirst()) {
            do {
                playlist = new Playlist();
                playlist.setId(queryCursor.getInt(queryCursor.getColumnIndex(DBMusicSchema.TablePlaylist.COL_ID)));
                playlist.setName(queryCursor.getString(queryCursor.getColumnIndex(DBMusicSchema.TablePlaylist.COL_NAME)));
                playlists.add(playlist);
            }while (queryCursor.moveToNext());
        }
        queryCursor.close();
        return playlists;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Song> getPlaylistSongs(int playlistId) {
        ArrayList<Integer> list_song_ids = new ArrayList<>();
        db = this.getWritableDatabase();
        String query = "SELECT " + DBMusicSchema.TablePlaylistSong.COL_SONG_ID + " FROM " + DBMusicSchema.TablePlaylistSong.TABLE_NAME +
                " WHERE " + DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + "=" + playlistId;
        Cursor queryCursor = db.rawQuery(query, null);
        if (queryCursor.moveToFirst()) {
            do {
                int song_id = queryCursor.getInt(queryCursor.getColumnIndex(DBMusicSchema.TablePlaylistSong.COL_SONG_ID));
                list_song_ids.add(song_id);
            } while (queryCursor.moveToNext());
        }
        ArrayList<Song> songs = (ArrayList<Song>) all_songs.stream()
                .filter((song) -> list_song_ids.contains(song.getId()))
                .collect(Collectors.toList());
        return songs;
    }

    public int updatePlaylist(Playlist playlist) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBMusicSchema.TablePlaylist.COL_NAME, playlist.getName());
        String where_cls = DBMusicSchema.TablePlaylist.COL_ID + "=?";
        String[] where_args = new String[]{Integer.toString(playlist.getId())};
        return db.update(DBMusicSchema.TablePlaylist.TABLE_NAME, contentValues, where_cls, where_args);
    }
}
