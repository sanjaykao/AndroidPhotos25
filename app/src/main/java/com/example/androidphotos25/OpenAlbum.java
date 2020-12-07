package com.example.androidphotos25;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class OpenAlbum extends AppCompatActivity {

    private User user;
    private ArrayList<Album> albums;
    private Album album;
    private ArrayList<Photo> photos;
    public RecyclerViewAdapter adapter1;
    public RecyclerView rv1;
    private String title;

    private static final int PICK_IMAGE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        title = intent.getStringExtra("Album name");
        user = (User)intent.getSerializableExtra("User");
        albums = user.getAlbums();
        setTitle(title);

        for(int i = 0; i < albums.size(); i++){
            if(albums.get(i).getAlbumName().equals(title)){
                this.album = albums.get(i);
                photos = album.getPhotos();
            }
        }

        rv1 = (RecyclerView)findViewById(R.id.album_rv_id);
        adapter1 = new RecyclerViewAdapter(this, user, album, photos,1);
        rv1.setLayoutManager(new GridLayoutManager(this, 3));
        rv1.setAdapter(adapter1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                addPhoto();
                return true;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchPage.class);
                intent.putExtra("User", user);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addPhoto(){
        Intent gal = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gal, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            String uri = intent.getData().toString();
            boolean exists = false;
            for(Photo photo : photos){
                if(photo.getPhotoName().equals(uri)){
                    exists = true;
                    Bundle bundle = new Bundle();
                    bundle.putString(PhotosDialogFragment.MESSAGE_KEY, "Photo already exists in this album!");
                    DialogFragment newFragment = new PhotosDialogFragment();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(), "badfields");
                    return;
                }
            }
            if(!exists){
                int index = photos.size();
                System.out.println(index);
                Photo temp = new Photo(uri);
                album.addPhotoToAlbum(temp);
                ObjectOutputStream oos;
                try {
                    oos = new ObjectOutputStream(new FileOutputStream(getFilesDir().getAbsolutePath() + File.separator + "userData.dat", false));
                    oos.writeObject(user);
                    oos.close();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
                adapter1.notifyItemInserted(index);
            }
        }
        if(resultCode == Activity.RESULT_OK){
            user = (User)intent.getSerializableExtra("User");
            albums = user.getAlbums();
            for(Album temp : albums){
                if(temp.getAlbumName().equals(title)){
                    album = temp;
                    photos = album.getPhotos();
                    break;
                }
            }
            adapter1 = new RecyclerViewAdapter(this, user, album, photos, 1);
            rv1.setAdapter(adapter1);
        }
    }

    @Override
    public void onBackPressed(){
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(getFilesDir().getAbsolutePath() + File.separator + "userData.dat"));
            user = (User)ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("User", user);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }
}