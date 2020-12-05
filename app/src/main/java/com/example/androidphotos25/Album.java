package com.example.androidphotos25;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    public String albumName;
    public int numOfPhotos;
    public ArrayList<Photo> photos;

    public Album(String name) {
        albumName = name;
        photos = new ArrayList<Photo>();
        numOfPhotos = 0;
    }

    public Album(String name, ArrayList<Photo> pics) {
        albumName = name;
        photos = pics;
        numOfPhotos = pics.size();
    }

    public void addPhotoToAlbum(Photo pic) {
        photos.add(pic);
        numOfPhotos++;
    }

    public void deletePhoto(Uri photo) {
        for(Photo item : photos) {
            if(item.getPhotoName().equals(photo)) {
                photos.remove(item);
                numOfPhotos--;
                break;
            }
        }
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String newName) {
        this.albumName = newName;
    }

    public int getNumOfPhotos() {
        return numOfPhotos;
    }

    public void setNumOfPhotos(int newNum) {
        this.numOfPhotos = newNum;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }
}
