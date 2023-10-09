package com.example.myapplication;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.db.entity.Hike;

import java.util.zip.Inflater;

public class ObservationActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hike_detail);


        TextView hikeName = findViewById(R.id.hikeName);
        TextView hikeLocation = findViewById(R.id.hikeLocation);
        TextView hikeDate = findViewById(R.id.hikeDate);
        TextView hikeParking = findViewById(R.id.hikeParking);
        TextView hikeLength = findViewById(R.id.hikeLength);
        TextView hikeDifficulty = findViewById(R.id.hikeDifficulty);
        TextView hikeDescription = findViewById(R.id.hikeDescription);

        //get radio group from popup_layout


        // Retrieve data passed from the source activity
        Intent intent = getIntent();
        if (intent != null) {

            long hikeId = intent.getLongExtra("hikeId", -1);
            db = new DatabaseHelper(this);
            Hike hike = db.getHike(hikeId);

            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View popupView = layoutInflater.inflate(R.layout.popup_layout, null);
            RadioGroup hikeDifficultyGroup = popupView.findViewById(R.id.popupHikeDifficulty);
            RadioButton selectedDifficulty = popupView.findViewById(hike.getDifficulty());
            String selectedRadioButtonText = selectedDifficulty.getText().toString();


            //render hike's details

            hikeName.setText("Hike name: " + hike.getName());
            hikeLocation.setText("Hike location: " + hike.getLocation());
            hikeDate.setText("Hike date: " + hike.getDate());
            hikeParking.setText("Parking available: " + (hike.getParking() ? "Yes" : "No"));
            hikeLength.setText("Hike length: " + hike.getLength());
            hikeDifficulty.setText("Hike difficulty: " + selectedRadioButtonText);
            hikeDescription.setText("\"" + hike.getDescription() + "\"");
        }
    }
}
