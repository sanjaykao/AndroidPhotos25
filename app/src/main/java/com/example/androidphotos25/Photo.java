package com.example.androidphotos25;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {
    public String photoName;
    public ArrayList<Tag> tags;
    public ArrayList<Album> albums;

    public Photo(String name) {
        this.photoName = name;
        this.tags = new ArrayList<Tag>();
        this.albums = new ArrayList<Album>();
    }

    public String getPhotoName() {
        return photoName;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }
}
