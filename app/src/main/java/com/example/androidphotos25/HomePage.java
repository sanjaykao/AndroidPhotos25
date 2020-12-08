package com.example.androidphotos25;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    private User user;
    private ArrayList<Album> albums;
    private String name;
    public RecyclerViewAdapter adapter;
    public RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

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
        if(user == null) {
            user = new User("user");
        }
        albums = user.getAlbums();

        rv = (RecyclerView)findViewById(R.id.rv_id);
        adapter = new RecyclerViewAdapter(this, user, albums, 0);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addAlbum();
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

    private void addAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create album");
        final EditText in = new EditText(this);
        in.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(in);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = in.getText().toString();
                for(Album album : albums) {
                    if (album.getAlbumName().equals(name)) {
                        Bundle bundle = new Bundle();
                        bundle.putString(PhotosDialogFragment.MESSAGE_KEY, "Album name already exists!");
                        DialogFragment newFragment = new PhotosDialogFragment();
                        newFragment.setArguments(bundle);
                        newFragment.show(getSupportFragmentManager(), "badfields");
                        return;
                    }
                }
                int ind = albums.size();
                user.createAlbum(name);
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
                adapter.notifyItemInserted(ind);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //adapter.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            user = (User)data.getSerializableExtra("User");
            albums = user.getAlbums();
            adapter = new RecyclerViewAdapter(this, user, albums, 0);
            rv.setAdapter(adapter);
        }
    }

}