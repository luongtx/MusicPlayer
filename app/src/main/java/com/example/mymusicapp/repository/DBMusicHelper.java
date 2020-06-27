package com.example.mymusicapp.repository;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mymusicapp.MusicProvider;
import com.example.mymusicapp.entity.Song;
import com.example.mymusicapp.entity.Playlist;

import java.util.ArrayList;

public class DBMusicHelper extends SQLiteOpenHelper {

    private Context context;
    SQLiteDatabase db;
    MusicProvider musicProvider;
    ArrayList<Song> all_songs;
    public DBMusicHelper(Context context) {
        super(context, DBMusicSchema.DB_NAME, null, 1);
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
                + DBMusicSchema.TablePlaylistSong.COL_SONG_ID + "integer" + ","
                + DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + "integer"
                + ");";
        db.execSQL(sql2);
        Log.d("DB CREATION", "db created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db = this.getWritableDatabase();
        String sql = "DROP TABLE IF EXISTS "+ DBMusicSchema.TablePlaylist.TABLE_NAME;
        db.execSQL(sql);
    }

    public void addPlaylist(Playlist playList) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBMusicSchema.TablePlaylist.COL_NAME, playList.getName());
        db.insert(DBMusicSchema.TablePlaylist.TABLE_NAME, null, values);
    }

    public void addSongToPlaylist(Song song) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBMusicSchema.TablePlaylistSong.COL_SONG_ID, song.getId());
        values.put(DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID, song.getPlaylist_id());
        db.insert(DBMusicSchema.TablePlaylistSong.TABLE_NAME, null, values);
    }

    public void deletePlaylist(int playlistId) {
        db = this.getWritableDatabase();
        String where_cls = DBMusicSchema.TablePlaylist.COL_ID + "=?";
        String[] where_args = new String[] {String.valueOf(playlistId)};
        db.delete(DBMusicSchema.TablePlaylist.TABLE_NAME, where_cls, where_args);
    }

    public void deleteSongFromPlaylist(int playlistId, int songId) {
        db = this.getWritableDatabase();
        String where_cls = DBMusicSchema.TablePlaylistSong.COL_SONG_ID + "=?" + " AND " + DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + "=?";
        String[] where_args = new String[]{String.valueOf(playlistId), String.valueOf(songId)};
        db.delete(DBMusicSchema.TablePlaylistSong.TABLE_NAME, where_cls, where_args);
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
        return playlists;
    }


    public ArrayList<Song> getPlaylistSongs(int playlistId) {
        ArrayList<Song> songs = new ArrayList<>();
        db = this.getWritableDatabase();
        String query = "SELECT * FROM " + DBMusicSchema.TablePlaylistSong.TABLE_NAME +
                " WHERE " + DBMusicSchema.TablePlaylistSong.COL_PLAYLIST_ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(playlistId)};
        Cursor queryCursor = db.rawQuery(query, selectionArgs);
        Song song;
        if(queryCursor.moveToFirst()) {
            do {
                int song_id = queryCursor.getInt(queryCursor.getColumnIndex(DBMusicSchema.TablePlaylistSong.COL_SONG_ID));
                song = all_songs.get(song_id);
                songs.add(song);
            } while(queryCursor.moveToNext());
        }
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
