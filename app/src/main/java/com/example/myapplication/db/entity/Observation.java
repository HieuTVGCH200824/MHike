package com.example.myapplication.db.entity;

public class Observation {

    public static final String TABLE_NAME ="observations";
    public static final String COLUMN_ID = "observation_id";
    public static final String COLUMN_NAME = "observation_name";
    public static final String COLUMN_TIME = "observation_time";
    public static final String COLUMN_COMMENT = "observation_comment";
    public static final String COLUMN_HIKE_ID = "observation_hike_id";

    private String name;
    private String time;
    private String comment;
    private long hikeId;

    private long id;

    public Observation() {

    }

    public Observation(String name, String time, String comment, long hikeId, long id) {
        this.name = name;
        this.time = time;
        this.comment = comment;
        this.hikeId = hikeId;
        this.id = id;
    }

    //get - set

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getHikeId() {
        return hikeId;
    }

    public void setHikeId(long hikeId) {
        this.hikeId = hikeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_TIME + " TEXT,"
                    + COLUMN_COMMENT + " TEXT,"
                    + COLUMN_HIKE_ID + " INTEGER,"
                    + "FOREIGN KEY(" + COLUMN_HIKE_ID + ") REFERENCES hikes(hike_id)"
                    + ")";

}
