package com.example.androidphotos25;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    public String username;
    public ArrayList<Album> albums;
    public ArrayList<Tag> userTags;

    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<Album>();
        this.userTags = new ArrayList<Tag>();
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Album> getAlbums(){
        return albums;
    }

    public ArrayList<Tag> getAlbumTags(){
        return userTags;
    }

    public void createAlbum(String name) {
        Album newAlbum = new Album(name);
        albums.add(newAlbum);
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public void deleteAlbum(String name) {
        String indexName;
        for(int i = 0; i < albums.size(); i++) {
            indexName = albums.get(i).getAlbumName();
            if(indexName.equals(name)) {
                albums.remove(i);
            }
        }
    }

    public void renameAlbum(Album album, String newName) {
        album.setAlbumName(newName);
    }

    public void addTag(Photo photo, String name, String value) {
        ArrayList<Tag> tags = photo.getTags();
        Tag newTag = new Tag(name, value);
        tags.add(newTag);
        addAlbumTag(newTag);
    }

    public void addAlbumTag(Tag tag) {
        for(Tag item : userTags) {
            if(item.getName().equals(tag.getName()) && item.getValue().equals(tag.getValue())) {
                return;
            }
        }
        userTags.add(tag);
    }

    public void deleteTag(Photo photo, String name, String value) {
        ArrayList<Tag> tags = photo.getTags();
        for(int i = 0; i < tags.size(); i++) {
            if(tags.get(i).getName().equals(name) && tags.get(i).getValue().equals(value)) {
                String temp = tags.get(i).getName() + ":" + tags.get(i).getValue();
                tags.remove(i);
                deleteUserTag(temp);
                break;
            }
        }
    }

    public void deleteUserTag(String tag) {
        // deletes the tag from the user tags arraylist if no more photos have the tag
        String name = tag.substring(0, tag.indexOf(':'));
        String value = tag.substring(tag.indexOf(':') + 1);
        for(Album album : albums) {
            ArrayList<Photo> photos = album.getPhotos();
            for(Photo photo : photos) {
                ArrayList<Tag> tags = photo.getTags();
                for(Tag item : tags) {
                    if(item.getName().equals(name) && item.getValue().equals(value)) {
                        return;
                    }
                }
            }
        }
        for(Tag uTag : userTags) {
            if(uTag.getName().equals(name) && uTag.getValue().equals(value)) {
                userTags.remove(uTag);
                break;
            }
        }
    }

    public void copyPhoto(Album dest, Photo photo) {
        dest.addPhotoToAlbum(photo);
        photo.getAlbums().add(dest);
    }
    
    public void movePhoto(Album dest, Album source, Photo photo) {
        dest.addPhotoToAlbum(photo);
        photo.getAlbums().add(dest);
        photo.getAlbums().remove(source);
        source.deletePhoto(photo.getPhotoName());
    }
}
