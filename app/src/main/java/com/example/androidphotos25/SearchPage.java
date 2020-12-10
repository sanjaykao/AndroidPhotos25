package com.example.androidphotos25;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private User user;
    private ArrayList<Album> albums;
    private String type;
    public RecyclerViewAdapter adapter1;
    public RecyclerView rv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("User");
        albums = user.getAlbums();

        Spinner spinner = (Spinner) findViewById(R.id.tags_spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> tagTypes = new ArrayList<String>();
        tagTypes.add("Person");
        tagTypes.add("Location");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tagTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //test cases - if input is valid (needs a tag type)
                //display search results
                //if display is none, display text or Toast text that no results
                EditText simpleEditText = (EditText) findViewById(R.id.tag_value);
                String strValue = simpleEditText.getText().toString();
                if(type == null) {
                    Toast.makeText(getApplicationContext(), "Please select a tag type", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<Photo> results = findSearchResults(type, strValue);
                    if(results.size() == 0) {
                        Toast.makeText(getApplicationContext(), "No photos match the results", Toast.LENGTH_LONG).show();
                    } else {
                        //display the results
                        displayResults(results);
                    }
                }
            }
        });
    }

    public void displayResults(ArrayList<Photo> results) {
        rv1 = (RecyclerView)findViewById(R.id.search_rv_id);
        adapter1 = new RecyclerViewAdapter(this, user, null, results,2);
        rv1.setLayoutManager(new GridLayoutManager(this, 3));
        rv1.setAdapter(adapter1);
    }

    public ArrayList<Photo> findSearchResults(String type, String value) {
        ArrayList<Photo> picResults = new ArrayList<Photo>();

        for(Album alb : albums) {
         for(Photo pic : alb.getPhotos()) {
             for(Tag tag : pic.getTags()) {
                 if(tag.getName().equals(type)) {
                     if(tag.getValue().contains(value)) {
                         picResults.add(pic);
                         break;
                     }
                 }
             }
         }
        }

        return picResults;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        type = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), "Type: " + type, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
