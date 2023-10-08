package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.HikeAdapter;
import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.db.entity.Hike;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addNewHike;
    private DatabaseHelper db;
    private HikeAdapter hikeAdapter;
    private ArrayList<Hike> hikeArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //render hike list
        RecyclerView hikeList = findViewById(R.id.recycler_view_hikes);
        db = new DatabaseHelper(this);

        hikeArrayList.addAll(db.getAllHikes());

        hikeAdapter = new HikeAdapter(this, hikeArrayList, this);
        hikeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        hikeList.setItemAnimator(new DefaultItemAnimator());
        hikeList.setAdapter(hikeAdapter);


        //delete all hikes
        Button deleteAllHikes = findViewById(R.id.deleteAllHikes);
        deleteAllHikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete All Hikes");
                builder.setMessage("Are you sure you want to delete all hikes?");
                builder.setCancelable(true);
                //delete on confirm
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        deleteAllHikes(view);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        addNewHike = findViewById(R.id.addNewHike);
        addNewHike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditHike(false, null, -1);
            }
        });

    }

    public void deleteAllHikes(View view) {
        db.deleteAllHikes();
        hikeArrayList.clear();
        hikeAdapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(Button hikeDateButton) {
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        hikeDateButton.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        datePickerDialog.show();
    }

    public void addAndEditHike (final boolean isUpdated, final Hike hike, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View popupView = layoutInflater.inflate(R.layout.popup_layout, null);
        //AlertDialog.builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(popupView);

        TextView popupTitle = popupView.findViewById(R.id.popupTitle);
        final TextView hikeName = popupView.findViewById(R.id.popupHikeName);
        final TextView hikeLocation = popupView.findViewById(R.id.popupHikeLocation);
        final Button hikeDateButton = popupView.findViewById(R.id.popupHikeDateButton);
        final CheckBox hikeParking = popupView.findViewById(R.id.popupParkingAvailable);
        final TextView hikeLength = popupView.findViewById(R.id.popupHikeLength);
        final RadioGroup hikeDifficulty = popupView.findViewById(R.id.popupHikeDifficulty);
        final TextView hikeDescription = popupView.findViewById(R.id.popupHikeDescription);

        hikeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(hikeDateButton);
            }
        });

        popupTitle.setText(isUpdated? "Edit Hike" : "Add New Hike");

        if (isUpdated && hike != null) {
            hikeName.setText(hike.getName());
            hikeLocation.setText(hike.getLocation());
            hikeDateButton.setText(hike.getDate());
            hikeParking.setChecked(hike.getParking());
            hikeLength.setText(hike.getLength());
            hikeDescription.setText(hike.getDescription());

            int difficulty = hike.getDifficulty();
            Log.d("difficulty", String.valueOf(difficulty));
            if(difficulty != 1){
                RadioButton radioButton = popupView.findViewById(difficulty);
                radioButton.setChecked(true);
            }
        }

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(TextUtils.isEmpty(hikeName.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.dismiss();
                        }

                        if(hikeName.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, "Hike's name invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(hikeLocation.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, "Hike's location invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(hikeDateButton.getText().toString().isEmpty() || hikeDateButton.getText().toString().equals("Select Date")){
                            Toast.makeText(MainActivity.this, "Hike's date invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(hikeLength.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's length invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeDifficulty.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(MainActivity.this, "Hike's difficulty invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(isUpdated && hike != null) {
                            updateHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hikeDateButton.getText().toString(), hikeParking.isChecked(), hikeLength.getText().toString(), hikeDifficulty.getCheckedRadioButtonId(), hikeDescription.getText().toString(), position);
                        } else {
                            createHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hikeDateButton.getText().toString(), hikeParking.isChecked(), hikeLength.getText().toString(), hikeDifficulty.getCheckedRadioButtonId(), hikeDescription.getText().toString());
                        }
                    }
                }).setNegativeButton(isUpdated ? "Delete" : "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isUpdated) {
                            deleteHike(hike, position);
                        } else {
                            dialogInterface.cancel();
                        }
                    }
                });
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
    }

    private void createHike (String name, String location, String date, Boolean parking, String length, int difficulty, String description) {
        long id = db.insertHike(name, location, date, parking, length, difficulty, description);
        Hike hike = db.getHike(id);
        if (hike != null) {
            hikeArrayList.add(0, hike);
            hikeAdapter.notifyDataSetChanged();
        }
    }

    private void updateHike (String name, String location, String date, Boolean parking, String length, int difficulty, String description, int position) {
        Hike hike = hikeArrayList.get(position);
        hike.setName(name);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParking(parking);
        hike.setLength(length);
        hike.setDifficulty(difficulty);
        hike.setDescription(description);

        db.updateHike(hike);
        hikeArrayList.set(position, hike);
        hikeAdapter.notifyDataSetChanged();

    }

    private void deleteHike (Hike hike, int position) {
        db.deleteHike(hike);
        hikeArrayList.remove(position);
        hikeAdapter.notifyDataSetChanged();
    }

}