package com.example.mymusicapp;

public class Artist {
    private int id;
    private String name;
    private int numberOfSongs;

    public Artist(int id, String name, int numberOfSongs) {
        this.id = id;
        this.name = name;
        this.numberOfSongs = numberOfSongs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

}
