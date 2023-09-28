package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.HikeAdapter;
import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.db.entity.Hike;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton addNewHike;
    private PopupWindow popupWindow;
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
        hikeList.setAdapter(new HikeAdapter(this, db.getAllHikes(), this));


        addNewHike = findViewById(R.id.addNewHike);
        addNewHike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditHike(false, null, -1);
            }
        });

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
        final TextView hikeDate = popupView.findViewById(R.id.popupHikeDate);
        final TextView hikeParking = popupView.findViewById(R.id.popupParkingAvailable);
        final TextView hikeLength = popupView.findViewById(R.id.popupHikeLength);
        final RadioGroup hikeDifficulty = popupView.findViewById(R.id.popupHikeDifficulty);
        final TextView hikeDescription = popupView.findViewById(R.id.popupHikeDescription);

        popupTitle.setText(isUpdated? "Edit Hike" : "Add New Hike");

        if (isUpdated && hike != null) {
            hikeName.setText(hike.getName());
            hikeLocation.setText(hike.getLocation());
            hikeDate.setText(hike.getDate());
            hikeParking.setText(hike.getParking());
            hikeLength.setText(hike.getLength());
            hikeDescription.setText(hike.getDescription());

            String difficulty = hike.getDifficulty();
            if ("Easy".equals(difficulty)) {
                hikeDifficulty.check(R.id.popupHikeDifficultyEasy);
            } else if ("Medium".equals(difficulty)) {
                hikeDifficulty.check(R.id.popupHikeDifficultyMedium);
            } else if ("Hard".equals(difficulty)) {
                hikeDifficulty.check(R.id.popupHikeDifficultyHard);
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

                        if(isUpdated && hike != null) {
                            updateHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hikeDate.getText().toString(), hikeParking.getText().toString(), hikeLength.getText().toString(), hikeDifficulty.toString(), hikeDescription.getText().toString(), position);
                        } else {
                            createHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hikeDate.getText().toString(), hikeParking.getText().toString(), hikeLength.getText().toString(), hikeDifficulty.toString(), hikeDescription.getText().toString());
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

    private void createHike (String name, String location, String date, String parking, String length, String difficulty, String description) {
        long id = db.insertHike(name, location, date, parking, length, difficulty, description);
        Hike hike = db.getHike(id);
        if (hike != null) {
            hikeArrayList.add(0, hike);
            hikeAdapter.notifyDataSetChanged();
        }
    }

    private void updateHike (String name, String location, String date, String parking, String length, String difficulty, String description, int position) {
        Hike hike = db.getAllHikes().get(position);
        hike.setName(name);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParking(parking);
        hike.setLength(length);
        hike.setDifficulty(difficulty);
        hike.setDescription(description);

        db.updateHike(hike);
        hikeAdapter.notifyDataSetChanged();
    }

    private void deleteHike (Hike hike, int position) {
        db.deleteHike(hike);
        hikeArrayList.remove(position);
        hikeAdapter.notifyDataSetChanged();
    }

}