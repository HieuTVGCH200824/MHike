package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mhike");


        //render hike list
        RecyclerView hikeList = findViewById(R.id.recycler_view_hikes);
        db = new DatabaseHelper(this);

        hikeArrayList.addAll(db.getAllHikes());

        hikeAdapter = new HikeAdapter(this, hikeArrayList, this);
        hikeList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        hikeList.setItemAnimator(new DefaultItemAnimator());
        hikeList.setAdapter(hikeAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView

                int position = viewHolder.getAdapterPosition();
                Hike hike = hikeArrayList.get(position);
                Log.d("Hike", "onSwiped: " + hike.getName());
                Log.d("position", "onSwiped: " + position);
                deleteHike(hike, position);
            }

            @Override
            public void onChildDraw(
                    @NonNull Canvas c,
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    float dX,
                    float dY,
                    int actionState,
                    boolean isCurrentlyActive
            ) {
                // Limit the swipe range to the width of the trash can icon (100 pixels)
                float yourMaxSwipeRange = 200f; // Adjust this to match the width of your icon

                if (Math.abs(dX) > yourMaxSwipeRange) {
                    dX = Math.signum(dX) * yourMaxSwipeRange;
                }

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (dX > 0) {
                    paint.setColor(Color.parseColor("#f43f5e"));
                    c.drawRect(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom(), paint);
                } else {
                    paint.setColor(Color.parseColor("#f43f5e"));
                    c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);
                }

                // Draw the trash can icon
                Drawable icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconLeft, iconTop, iconRight, iconBottom;

                if (dX > 0) {
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                } else {
                    iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                }

                iconRight = iconLeft + icon.getIntrinsicWidth();
                iconBottom = iconTop + icon.getIntrinsicHeight();

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(hikeList);

        //delete all hikes
        ImageButton deleteAllHikes = findViewById(R.id.deleteAllHikes);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hikeArrayList.clear();
                hikeArrayList.addAll(db.getAllHikes());
                hikeAdapter.filterList(hikeArrayList);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //hide add button when searching
                filter(newText);
                return false;
            }
        });

        //reshow button when exit search
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                addNewHike.setVisibility(View.VISIBLE);
                return false;
            }
        });
        return true;
    }

    //filter hike list
    private void filter(String text) {
        ArrayList<Hike> filteredList = new ArrayList<>();

        for (Hike hike : hikeArrayList) {
            if (hike.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(hike);
            }
        }


        if(text.length() == 0){
           //refesh list
            hikeArrayList.clear();
            hikeArrayList.addAll(db.getAllHikes());
            hikeAdapter.filterList(hikeArrayList);
        }else{
        hikeAdapter.filterList(filteredList);
        }

    }

    public void deleteAllHikes(View view) {
        db.deleteAllHikes();
        hikeArrayList.clear();
        hikeAdapter.deleteAllHikes();
    }

    private void showDatePickerDialog(Button hikeDateButton) {
        final Calendar c = Calendar.getInstance();


        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        hikeDateButton.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                },

                year, month, day);

        datePickerDialog.show();
    }

    public void addAndEditHike(final boolean isUpdated, final Hike hike, final int position) {
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

        popupTitle.setText(isUpdated ? "Edit Hike" : "Add New Hike");

        if (isUpdated && hike != null) {
            hikeName.setText(hike.getName());
            hikeLocation.setText(hike.getLocation());
            hikeDateButton.setText(hike.getDate());
            hikeParking.setChecked(hike.getParking());
            hikeLength.setText(hike.getLength());
            hikeDescription.setText(hike.getDescription());

            int difficulty = hike.getDifficulty();
            if (difficulty != 1) {
                RadioButton radioButton = popupView.findViewById(difficulty);
                radioButton.setChecked(true);
            }
        }

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(hikeName.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.dismiss();
                        }

                        if (hikeName.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's name invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLocation.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's location invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeDateButton.getText().toString().isEmpty() || hikeDateButton.getText().toString().equals("Select Date")) {
                            Toast.makeText(MainActivity.this, "Hike's date invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLength.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's length invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeDifficulty.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(MainActivity.this, "Hike's difficulty invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (isUpdated && hike != null) {
                            updateHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hikeDateButton.getText().toString(), hikeParking.isChecked(), hikeLength.getText().toString(), hikeDifficulty.getCheckedRadioButtonId(), hikeDescription.getText().toString(), position);
                        } else {
                            createHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hikeDateButton.getText().toString(), hikeParking.isChecked(), hikeLength.getText().toString(), hikeDifficulty.getCheckedRadioButtonId(), hikeDescription.getText().toString());
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

    private void createHike(String name, String location, String date, Boolean parking, String length, int difficulty, String description) {
        long id = db.insertHike(name, location, date, parking, length, difficulty, description);
        Hike hike = db.getHike(id);
        if (hike != null) {
            hikeAdapter.createHike(hike);
        }
    }

    private void updateHike(String name, String location, String date, Boolean parking, String length, int difficulty, String description, int position) {
        Hike hike = hikeArrayList.get(position);
        hike.setName(name);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParking(parking);
        hike.setLength(length);
        hike.setDifficulty(difficulty);
        hike.setDescription(description);

        db.updateHike(hike);
        hikeAdapter.updateHike(hike, position);

    }

    private void deleteHike(Hike hike, int position) {
        //create confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete Hike");
        builder.setMessage("Are you sure you want to delete this hike?");
        builder.setCancelable(true);
        //delete on confirm
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                db.deleteHike(hike);
                hikeAdapter.deleteHike(position);
                dialog.dismiss();
            }
        });
        //cancel delete
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                hikeAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

}