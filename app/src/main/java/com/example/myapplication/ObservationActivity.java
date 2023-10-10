package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.ObservationAdapter;
import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.db.entity.Hike;
import com.example.myapplication.db.entity.Observation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;

public class ObservationActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private FloatingActionButton addNewObs;
    private ObservationAdapter observationAdapter;

    private ArrayList<Observation> observationArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hike_detail);

        //add return button to toolbar w copilot
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Hike Details");


        TextView hikeName = findViewById(R.id.hikeName);
        TextView hikeLocation = findViewById(R.id.hikeLocation);
        TextView hikeDate = findViewById(R.id.hikeDate);
        TextView hikeParking = findViewById(R.id.hikeParking);
        TextView hikeLength = findViewById(R.id.hikeLength);
        TextView hikeDifficulty = findViewById(R.id.hikeDifficulty);
        TextView hikeDescription = findViewById(R.id.hikeDescription);




        // Retrieve data passed from the source activity
        Intent intent = getIntent();
        if (intent != null) {

            RecyclerView obsList = findViewById(R.id.recycler_view_observations);
            long hikeId = intent.getLongExtra("hikeId", -1);
            db = new DatabaseHelper(this);
            Hike hike = db.getHike(hikeId);

            observationArrayList= db.getAllObservationsByHikeId(hikeId);

            observationAdapter = new ObservationAdapter(this, observationArrayList,this);
            obsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            obsList.setItemAnimator(new DefaultItemAnimator());
            obsList.setAdapter(observationAdapter);

            addNewObs = findViewById(R.id.addNewObservation);
            addNewObs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addAndEditObservation(false, null, -1,hikeId);
                }
            });

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

    public void addAndEditObservation(final boolean isUpdated, final Observation obs, final int position,final long hikeId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View popupView = layoutInflater.inflate(R.layout.obs_popup_layout, null);
        //AlertDialog.builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ObservationActivity.this);
        alertDialogBuilder.setView(popupView);

        TextView popupTitle = popupView.findViewById(R.id.popupTitle);
        final TextView obsName = popupView.findViewById(R.id.popupObsName);
        final Button obsTime = popupView.findViewById(R.id.popupObsTime);
        final TextView obsComment = popupView.findViewById(R.id.popupObsComment);

        obsTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(obsTime);
            }
        });

        popupTitle.setText(isUpdated ? "Edit Hike" : "Add New Hike");

        if (isUpdated && obs != null) {
            obsName.setText(obs.getName());
            obsTime.setText(obs.getTime());
            obsComment.setText(obs.getComment());

        }

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(obsName.getText().toString())) {
                            Toast.makeText(ObservationActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.dismiss();
                        }

                        if (obsName.getText().toString().isEmpty()) {
                            Toast.makeText(ObservationActivity.this, "Hike's name invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (obsTime.getText().toString().isEmpty()) {
                            Toast.makeText(ObservationActivity.this, "Hike's location invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (obsComment.getText().toString().isEmpty()) {
                            Toast.makeText(ObservationActivity.this, "Hike's date invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(isUpdated && obs != null){
                            updateObservation(obsName.getText().toString(), obsTime.getText().toString(), obsComment.getText().toString(), position);
                        } else {
                            createObservation(obsName.getText().toString(), obsTime.getText().toString(), obsComment.getText().toString(), hikeId);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void createObservation(String name, String time, String comment,long hikeId) {
        long id = db.insertObservation(name, time, comment,hikeId);
        Observation obs = db.getObservation(id);

        if (obs != null) {
            observationArrayList.add(0, obs);
            observationAdapter.notifyDataSetChanged();
        }
    }

    private void updateObservation(String name, String time, String comment, int position) {
        Observation obs = observationArrayList.get(position);
        obs.setName(name);
        obs.setTime(time);
        obs.setComment(comment);

        db.updateObservation(obs);
        observationArrayList.set(position, obs);
        observationAdapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(Button obsTime) {
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                ObservationActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        obsTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
