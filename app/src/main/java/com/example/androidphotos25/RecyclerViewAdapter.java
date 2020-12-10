package com.example.androidphotos25;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private User user;
    private ArrayList<Album> albums;

    private Album album;
    private ArrayList<Photo> photos;

    private String name;
    private int activity;

    private static final int ALBUM_CODE = 100;
    private static final int PHOTO_CODE = 101;

    public RecyclerViewAdapter(Context context, User user, ArrayList<Album> albums, int activity){
        this.context = context;
        this.user = user;
        this.albums = albums;
        this.activity = activity;
    }

    public RecyclerViewAdapter(Context context, User user, Album album, ArrayList<Photo> photos, int activity){
        this.context = context;
        this.user = user;
        this.album = album;
        this.photos = photos;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater infl = LayoutInflater.from(context);
        if(activity == 0){
            view = infl.inflate(R.layout.album_cards, parent, false);
        }else if (activity == 1){
            view = infl.inflate(R.layout.open_album_cards, parent, false);
        } else {
            view = infl.inflate(R.layout.search_cards, parent, false);
        }
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(activity == 0){
            ArrayList<Photo> photos1 = albums.get(position).getPhotos();
            if(photos1.size() > 0){
                holder.thumb.setImageURI(Uri.parse(photos1.get(photos1.size() - 1).getPhotoName()));
            }
            holder.name.setText(albums.get(position).getAlbumName());
            holder.cv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OpenAlbum.class);
                    intent.putExtra("Album name", albums.get(position).getAlbumName());
                    intent.putExtra("User", user);
                    ((Activity)context).startActivityForResult(intent, ALBUM_CODE);
                }
            });
            holder.viewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu pop = new PopupMenu(context, holder.viewMenu);
                    pop.inflate(R.menu.home_menu);
                    pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.rename:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Rename album");
                                    final EditText in = new EditText(context);
                                    in.setInputType(InputType.TYPE_CLASS_TEXT);
                                    builder.setView(in);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            boolean exists = false;
                                            name = in.getText().toString();
                                            for(int i = 0; i < albums.size(); i++) {
                                                if(i == position){
                                                    continue;
                                                }
                                                if (albums.get(i).getAlbumName().equals(name)) {
                                                    exists = true;
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(PhotosDialogFragment.MESSAGE_KEY, "Album name already exists!");
                                                    DialogFragment newFragment = new PhotosDialogFragment();
                                                    newFragment.setArguments(bundle);
                                                    newFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "badfields");
                                                    return;
                                                }
                                            }
                                            if(!exists) {
                                                user.renameAlbum(albums.get(position), name);
                                                ObjectOutputStream oos;
                                                try {
                                                    oos = new ObjectOutputStream(new FileOutputStream(context.getFilesDir().getAbsolutePath() + File.separator + "userData.dat", false));
                                                    oos.writeObject(user);
                                                    oos.close();
                                                } catch (FileNotFoundException e) {
                                                    // TODO Auto-generated catch block
                                                    //e.printStackTrace();
                                                } catch (IOException e) {
                                                    // TODO Auto-generated catch block
                                                    //e.printStackTrace();
                                                }
                                                notifyItemChanged(position);
                                            }
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();
                                    break;
                                case R.id.home_delete:
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                    builder2.setTitle("Are you sure you want to delete this album?");
                                    builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            user.deleteAlbum(albums.get(position).getAlbumName());
                                            ObjectOutputStream oos;
                                            try {
                                                oos = new ObjectOutputStream(new FileOutputStream(context.getFilesDir().getAbsolutePath() + File.separator + "userData.dat", false));
                                                oos.writeObject(user);
                                                oos.close();
                                            } catch (FileNotFoundException e) {
                                                // TODO Auto-generated catch block
                                                //e.printStackTrace();
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                //e.printStackTrace();
                                            }
                                            notifyItemRemoved(position);
                                        }
                                    });
                                    builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder2.show();
                                    break;
                            }
                            return false;
                        }
                    });
                    pop.show();
                }
            });
        } else if (activity == 1){
            holder.photo_thumb.setImageURI(Uri.parse(photos.get(position).getPhotoName()));
            holder.album_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoDisplay.class);
                    intent.putExtra("Photo", photos.get(position));
                    intent.putExtra("User", user);
                    ((Activity)context).startActivityForResult(intent, PHOTO_CODE);
                }
            });
            holder.photo_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, holder.photo_menu);
                    popup.inflate(R.menu.open_album_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.move_photo:
                                    ArrayList<Album> temp3 = user.getAlbums();
                                    ArrayList<String> albs = new ArrayList<String>();
                                    for(Album alb : temp3){
                                        if(alb.getAlbumName().equals(album.getAlbumName())){
                                            continue;
                                        }else{
                                            albs.add(alb.getAlbumName());
                                        }
                                    }
                                    AlertDialog.Builder builder4 = new AlertDialog.Builder(context);
                                    builder4.setTitle("Choose where to move this photo");
                                    builder4.setItems(albs.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String alb_name = albs.get(which);
                                            for(Album item3 : temp3){
                                                if(item3.getAlbumName().equals(alb_name)){
                                                    boolean exists = false;
                                                    ArrayList<Photo> destPhotos = item3.getPhotos();
                                                    for(Photo pic : destPhotos){
                                                        if(pic.getPhotoName().equals(photos.get(position).getPhotoName())){
                                                            exists = true;
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString(PhotosDialogFragment.MESSAGE_KEY, "Photo already exists in this album!");
                                                            DialogFragment newFragment = new PhotosDialogFragment();
                                                            newFragment.setArguments(bundle);
                                                            newFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "badfields");
                                                            return;
                                                        }
                                                    }
                                                    if(!exists){
                                                        user.movePhoto(item3, album, photos.get(position));
                                                        ObjectOutputStream oos;
                                                        try {
                                                            oos = new ObjectOutputStream(new FileOutputStream(context.getFilesDir().getAbsolutePath() + File.separator + "userData.dat", false));
                                                            oos.writeObject(user);
                                                            oos.close();
                                                        } catch (FileNotFoundException e) {
                                                            // TODO Auto-generated catch block
                                                            //e.printStackTrace();
                                                        } catch (IOException e) {
                                                            // TODO Auto-generated catch block
                                                            //e.printStackTrace();
                                                        }
                                                        notifyItemRemoved(position);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    });
                                    builder4.show();
                                    break;
                                case R.id.open_album_delete:
                                    AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                                    builder3.setTitle("Are you sure you want to delete this photo?");
                                    builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            album.deletePhoto(photos.get(position).getPhotoName());
                                            ObjectOutputStream oos;
                                            try {
                                                oos = new ObjectOutputStream(new FileOutputStream(context.getFilesDir().getAbsolutePath() + File.separator + "userData.dat", false));
                                                oos.writeObject(user);
                                                oos.close();
                                            } catch (FileNotFoundException e) {
                                                // TODO Auto-generated catch block
                                                //e.printStackTrace();
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                //e.printStackTrace();
                                            }
                                            notifyItemRemoved(position);
                                        }
                                    });
                                    builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder3.show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        } else {
            holder.search_thumb.setImageURI(Uri.parse(photos.get(position).getPhotoName()));
        }
    }

    @Override
    public int getItemCount() {
        if(activity == 0){
            return albums.size();
        }else{
            return photos.size();
        }

    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            user = (User)data.getSerializableExtra("User");
            albums = user.getAlbums();
            notifyAll();
        }
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumb;
        public TextView name;
        public TextView viewMenu;
        public CardView cv;

        public ImageView photo_thumb;
        public TextView photo_menu;
        public CardView album_cv;
        public ImageView search_thumb;
        //public CardView search_cv_id;

        public ViewHolder(View view){
            super(view);
            thumb = (ImageView)view.findViewById(R.id.album_thumb);
            name = (TextView)view.findViewById(R.id.album_name);
            viewMenu = (TextView)view.findViewById(R.id.homepage_menu);
            cv = (CardView)view.findViewById(R.id.cv_id);

            photo_thumb = (ImageView)view.findViewById(R.id.album_pic_thumb);
            photo_menu = (TextView)view.findViewById(R.id.album_menu);
            album_cv = (CardView)view.findViewById(R.id.album_cv_id);

            search_thumb = (ImageView)view.findViewById(R.id.search_thumb);
        }
    }
}
