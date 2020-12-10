package com.example.androidphotos25;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoDisplay extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private User user;
    private Photo photo;
    //private ArrayList<Tag> tags;
    private String newTagType;
    ListView listView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("User");
        photo = (Photo)intent.getSerializableExtra("Photo");

        ImageView imageView = (ImageView) findViewById(R.id.photo_display_view);
        imageView.setImageURI(Uri.parse(photo.getPhotoName()));
        displayTags();

        Spinner spinner = (Spinner) findViewById(R.id.add_tags_spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> tagTypes = new ArrayList<String>();
        tagTypes.add("Person");
        tagTypes.add("Location");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tagTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //add tag to the list and update userData file
        Button addTagButton = (Button) findViewById(R.id.addTagButton);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText simpleEditText = (EditText) findViewById(R.id.add_tag_value);
                String newTagValue = simpleEditText.getText().toString();
                user.addTagToPhoto(photo, newTagType, newTagValue);
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
                displayTags();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String clickedTag = (String) listView.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoDisplay.this);
                builder.setTitle(clickedTag);
                builder.setPositiveButton("Delete Tag", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int colonIndex = clickedTag.indexOf(":");
                        String currentType = clickedTag.substring(0, colonIndex);
                        String currentValue = clickedTag.substring(colonIndex + 2);
                        user.deleteTag(photo, currentType, currentValue);
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
                        displayTags();
                    }
                });
                builder.setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }

        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, OpenAlbum.class);
        intent.putExtra("User", user);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        newTagType = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void displayTags() {
        listView = (ListView) findViewById(R.id.tagListView);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                photo.toStringTags());

        listView.setAdapter(adapter);
    }
}
