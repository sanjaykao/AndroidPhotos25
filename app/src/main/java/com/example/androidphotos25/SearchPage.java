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
    private String tagType1;
    private String tagType2;
    private String compType;
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

        Spinner spinnerTag1 = (Spinner) findViewById(R.id.tag1_spinner);
        spinnerTag1.setOnItemSelectedListener(this);
        Spinner spinnerTag2 = (Spinner) findViewById(R.id.tag2_spinner);
        spinnerTag2.setOnItemSelectedListener(this);
        Spinner spinnerComp = (Spinner) findViewById(R.id.compare_spinner);
        spinnerComp.setOnItemSelectedListener(this);

        List<String> tagTypes = new ArrayList<String>();
        tagTypes.add("Person");
        tagTypes.add("Location");

        List<String> compareTypes = new ArrayList<String>();
        compareTypes.add("SINGLE");
        compareTypes.add("AND");
        compareTypes.add("OR");

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tagTypes);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTag1.setAdapter(adapterType);
        spinnerTag2.setAdapter(adapterType);

        ArrayAdapter<String> adapterComp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, compareTypes);
        adapterComp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComp.setAdapter(adapterComp);

        Button searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //test cases - if input is valid (needs a tag type)
                //display search results
                //if display is none, display text or Toast text that no results
                EditText simpleEditText1 = (EditText) findViewById(R.id.tag1_value);
                String tagValue1 = simpleEditText1.getText().toString();
                EditText simpleEditText2 = (EditText) findViewById(R.id.tag2_value);
                String tagValue2 = simpleEditText2.getText().toString();

                //System.out.println("Tag 1: " + tagType1 + ", " + tagValue1);
                //System.out.println("Compare: " + compType);
                //System.out.println("Tag 2: " + tagType2 + ", " + tagValue2);
                /*ArrayList<Photo> test = albums.get(0).getPhotos();
                Photo dog = test.get(0);
                user.addTagToPhoto(dog, "Person", "reggie");
                user.addTagToPhoto(dog, "Location", "bed");
                Photo cs = test.get(1);
                user.addTagToPhoto(cs, "Person", "me");
                user.addTagToPhoto(cs, "Location", "dying");
                user.addTagToPhoto(cs, "Location", "hire");
                Photo job = test.get(2);
                user.addTagToPhoto(job, "Person", "employee");
                user.addTagToPhoto(job, "Location", "hire");
                user.addTagToPhoto(job, "Person", "reggie");*/

                if(tagType1 == null || tagType2 == null || compType == null) {
                    Toast.makeText(getApplicationContext(), "Please select a tag/compare type", Toast.LENGTH_LONG).show();
                } else if(tagValue1.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please input at least one tag", Toast.LENGTH_LONG).show();
                } else if(compType.equals("SINGLE")) {
                    ArrayList<Photo> picResults = findSearchResults(tagType1, tagValue1, null, null, compType);
                    if(picResults.size() == 0) {
                        Toast.makeText(getApplicationContext(), "No photos match the results", Toast.LENGTH_LONG).show();
                    } else {
                        displayResults(picResults);
                    }
                } else if(tagValue2.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please choose second tag for AND/OR comparison", Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<Photo> results = findSearchResults(tagType1, tagValue1, tagType2, tagValue2, compType);
                    if(results.size() == 0) {
                        Toast.makeText(getApplicationContext(), "No photos match the results", Toast.LENGTH_LONG).show();
                    } else {
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

    public ArrayList<Photo> findSearchResults(String type1, String value1, String type2, String value2, String compare) {
        ArrayList<Photo> picResults = new ArrayList<Photo>();

        for(Album currAlbum : albums) {
            for(Photo currPic : currAlbum.getPhotos()) {
                boolean firstTag = false;
                boolean secondTag = false;
                //iterate through to see if tags are found
                for(Tag currTag : currPic.getTags()) {
                    String currType = currTag.getName();
                    String currValue = currTag.getValue();

                    if(compare.equals("SINGLE") && currType.equals(type1) && currValue.contains(value1)) {
                        firstTag = true;
                        break;
                    }
                    else {
                        if(currType.equals(type1) && currValue.contains(value1)) {
                            firstTag = true;
                        }
                        if(currType.equals(type2) && currValue.contains(value2)) {
                            secondTag = true;
                        }
                    }
                }

                    //add photo if conditions are true
                    if(compare.equals("SINGLE")) {
                        if(firstTag) {
                            picResults.add(currPic);
                        }
                    } else if(compare.equals("AND")) {
                        if(firstTag && secondTag) {
                            picResults.add(currPic);
                        }
                    } else if(compare.equals("OR")) {
                        if(firstTag || secondTag) {
                            picResults.add(currPic);
                        }
                    }
                }
        }

        return picResults;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //type = parent.getItemAtPosition(position).toString();
        switch(parent.getId()) {
            case R.id.tag1_spinner:
                tagType1 = parent.getItemAtPosition(position).toString();
                break;
            case R.id.tag2_spinner:
                tagType2 = parent.getItemAtPosition(position).toString();
                break;
            case R.id.compare_spinner:
                compType = parent.getItemAtPosition(position).toString();
        }
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
