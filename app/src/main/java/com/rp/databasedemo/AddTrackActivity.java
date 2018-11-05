package com.rp.databasedemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {

    TextView textViewArtistName;
    EditText editTextTrackName;
    SeekBar seekBarRating;

    Button btnAddTrack;

    ListView listViewTracks;

    DatabaseReference databaseTracks;


    List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewArtistName = findViewById(R.id.textViewArtistName);
        editTextTrackName = findViewById(R.id.editTextTrackName);
        seekBarRating = findViewById(R.id.seekBarRating);

        btnAddTrack = findViewById(R.id.btnAddTrack);

        listViewTracks = findViewById(R.id.listViewTracks);

        Intent intent = getIntent();

        tracks = new ArrayList<>();
        String id = intent.getStringExtra(MainActivity.ARTIST_ID);
        String name = intent.getStringExtra(MainActivity.ARTIST_NAME);

        textViewArtistName.setText(name);

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        btnAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveTrack();

            }
        });

        listViewTracks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Track track = tracks.get(position);

                showUpdateDialog(track.getTrackId(), track.getTrackName(), track.getTrackRating());

                return true;

            }

        });

    }

    private void showUpdateDialog(final String trackId, final String trackName, final int rating){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog_tracks, null);

        dialogBuilder.setView(dialogView);

        final EditText editTextName = dialogView.findViewById(R.id.editTextName);

        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);

        final SeekBar seekBarRating = dialogView.findViewById(R.id.seekBarRating);

        seekBarRating.setProgress(rating);

        final Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        dialogBuilder.setTitle("Updating Track " + trackName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTextName.getText().toString().trim();
                int rating = seekBarRating.getProgress();

                if(TextUtils.isEmpty(name)){

                    editTextName.setError("Name required!");

                    return;

                }

                updateTrack(trackId, name, rating);

                alertDialog.dismiss();

            }

        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteTrack(trackId);

                alertDialog.dismiss();

            }

        });

    }

    private void deleteTrack(String trackId){

        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("tracks").child(trackId);

        drTracks.removeValue();

        Toast.makeText(this, "Track deleted!", Toast.LENGTH_LONG).show();

    }

    private boolean updateTrack(String id, String name, int rating){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        Track track = new Track(id, name, rating);

        databaseReference.setValue(track);

        Toast.makeText(this, "Track updated successfully!", Toast.LENGTH_LONG).show();

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tracks.clear();

                for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()){

                    Track track = trackSnapshot.getValue(Track.class);
                    tracks.add(track);

                }
                TrackList trackListAdapter = new TrackList(AddTrackActivity.this, tracks);
                listViewTracks.setAdapter(trackListAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveTrack(){

        String trackName = editTextTrackName.getText().toString().trim();

        int rating = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(trackName)){

            String id = databaseTracks.push().getKey();

            Track track = new Track(id, trackName, rating);

            databaseTracks.child(id).setValue(track);

            Toast.makeText(this, "Track saved successfully", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "Track name should not be empty", Toast.LENGTH_LONG).show();

        }
    }

}
