package com.example.mymusicapp.repository;

public class DBMusicSchema {
    static String DB_NAME = "db_music";

    static class TablePlaylist {
        static String TABLE_NAME = "tbl_playlists";

        static String COL_ID = "id";
        static String COL_NAME = "name";
    }

    static class TablePlaylistSong {
        static String TABLE_NAME = "tbl_song_playlist";

        static String COL_ID = "id";
        static String COL_SONG_ID = "song_id";
        static String COL_PLAYLIST_ID = "playlist_id";
    }
}
