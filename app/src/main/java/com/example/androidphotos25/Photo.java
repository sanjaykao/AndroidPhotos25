package com.example.androidphotos25;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {
    public Uri photoName;
    public ArrayList<Tag> tags;
    public ArrayList<Album> albums;

    public Photo(Uri name) {
        this.photoName = name;
        this.tags = new ArrayList<Tag>();
        this.albums = new ArrayList<Album>();
    }

    public Uri getPhotoName() {
        return photoName;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }
}
