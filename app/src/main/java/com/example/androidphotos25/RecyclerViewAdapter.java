package com.example.androidphotos25;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private User user;
    private ArrayList<Album> albums;

    public RecyclerViewAdapter(Context context, User user, ArrayList<Album> albums){
        this.context = context;
        this.user = user;
        this.albums = albums;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater infl = LayoutInflater.from(context);
        view = infl.inflate(R.layout.album_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArrayList<Photo> photos = albums.get(position).getPhotos();
        System.out.println(String.valueOf(photos.size()));
        if(photos.size() > 0){
            holder.thumb.setImageURI(photos.get(photos.size() - 1).getPhotoName());
        }
        holder.name.setText(albums.get(position).getAlbumName());
        holder.cv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpenAlbum.class);
                intent.putExtra("Album name", albums.get(position).getAlbumName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView thumb;
        TextView name;
        CardView cv;

        public ViewHolder(View view){
            super(view);
            thumb = (ImageView)view.findViewById(R.id.album_thumb);
            name = (TextView)view.findViewById(R.id.album_name);
            cv = (CardView)view.findViewById(R.id.cv_id);
        }
    }
}
