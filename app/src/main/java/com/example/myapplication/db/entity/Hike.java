package com.example.myapplication.db.entity;

import android.util.Log;

public class Hike {
//    Name of hike (e.g. "Snowdon•, "Trosley Country park", etc.) — Required field
//            Location - Required field
//            Date of the hike - Required field
//            Parking available (i.e. "Yes" or "NO") - Required field
//    Length the hike - Required field
//    Level of difficulty - Required field
//    Description — Optional field

    public static final String TABLE_NAME = "hikes";
    public static final String COLUMN_ID = "hike_id";
    public static final String COLUMN_NAME = "hike_name";
    public static final String COLUMN_LOCATION = "hike_location";
    public static final String COLUMN_DATE = "hike_date";
    public static final String COLUMN_PARKING = "hike_parking";
    public static final String COLUMN_LENGTH = "hike_length";
    public static final String COLUMN_DIFFICULTY = "hike_difficulty";
    public static final String COLUMN_DESCRIPTION = "hike_description";

    private String name;
    private String location;
    private String date;
    private Boolean parking;
    private String length;
    private int difficulty;
    private String description;
    private long id;

    public Hike() {

    }

    public Hike(String name, String location, String date, Boolean parking, String length, int difficulty, String description, long id) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.parking = parking;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_LOCATION + " TEXT,"
            + COLUMN_DATE + " TEXT,"
            + COLUMN_PARKING + " TEXT,"
            + COLUMN_LENGTH + " TEXT,"
            + COLUMN_DIFFICULTY + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT"
            + ")";
}
