package com.rp.databasedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    Button btnAddArtist;
    Spinner spinnerGenres;

    DatabaseReference databaseArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        editTextName = findViewById(R.id.editTextName);
        btnAddArtist = findViewById(R.id.btnAddArtist);
        spinnerGenres = findViewById(R.id.spinnerGenres);

        btnAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addArtist();

            }
        });

    }

    public void addArtist(){

        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenres.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name)){

            String id = databaseArtists.push().getKey();

            Artist artist = new Artist(id, name, genre);

            databaseArtists.child(id).setValue(artist);

            Toast.makeText(this, "Artist added!", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "You should enter a name!", Toast.LENGTH_LONG).show();

        }

    }

}
