package com.example.musicapplication.Model;

public class Album {
    String id;
    String image;
    String singer;
    String title;

    public Album(String id, String image, String singer, String title) {
        this.id = id;
        this.image = image;
        this.singer = singer;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
