package com.example.myapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapplication.db.entity.Hike;
import com.example.myapplication.db.entity.Observation;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "hike_db";

        public DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Hike.CREATE_TABLE);
        sqLiteDatabase.execSQL(Observation.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Hike.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Observation.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }


    // Getting Observation from DataBase
    public Observation getObservation(long id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Observation.TABLE_NAME,
                new String[]{
                        Observation.COLUMN_ID,
                        Observation.COLUMN_NAME,
                        Observation.COLUMN_TIME,
                        Observation.COLUMN_COMMENT,
                        Observation.COLUMN_HIKE_ID},
                Observation.COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null,
                null);

        if (cursor !=null)
        {
            cursor.moveToFirst();
        }

        Observation observation = new Observation(
                cursor.getString(cursor.getColumnIndexOrThrow(Observation.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Observation.COLUMN_TIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Observation.COLUMN_COMMENT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(Observation.COLUMN_HIKE_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Observation.COLUMN_ID))
        );

        cursor.close();
        return observation;
    }

    // insert Observation
    public long insertObservation(String name, String time, String comment, long hikeId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Observation.COLUMN_NAME, name);
        values.put(Observation.COLUMN_TIME, time);
        values.put(Observation.COLUMN_COMMENT, comment);
        values.put(Observation.COLUMN_HIKE_ID, hikeId);

        long id = db.insert(Observation.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // update Observation
    public int updateObservation(Observation observation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Observation.COLUMN_NAME, observation.getName());
        values.put(Observation.COLUMN_TIME, observation.getTime());
        values.put(Observation.COLUMN_COMMENT, observation.getComment());
        values.put(Observation.COLUMN_HIKE_ID, observation.getHikeId());

        return db.update(Observation.TABLE_NAME, values, Observation.COLUMN_ID + "=?", new String[]{String.valueOf(observation.getId())});
    }

    // delete Observation
    public void deleteObservation(Observation observation){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Observation.TABLE_NAME, Observation.COLUMN_ID + "=?", new String[]{String.valueOf(observation.getId())});
        db.close();
    }

    // delete all Observations

    public void deleteAllObservations(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Observation.TABLE_NAME);
        db.close();
    }

    // get all Observations by hikeId
    public ArrayList<Observation> getAllObservationsByHikeId(long hikeId){
        ArrayList<Observation> observations = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Observation.TABLE_NAME + " WHERE " + Observation.COLUMN_HIKE_ID + " = " + hikeId + " ORDER BY " + Observation.COLUMN_TIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Observation observation = new Observation();
                observation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Observation.COLUMN_ID)));
                observation.setName(cursor.getString(cursor.getColumnIndexOrThrow(Observation.COLUMN_NAME)));
                observation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(Observation.COLUMN_TIME)));
                observation.setComment(cursor.getString(cursor.getColumnIndexOrThrow(Observation.COLUMN_COMMENT)));
                observation.setHikeId(cursor.getLong(cursor.getColumnIndexOrThrow(Observation.COLUMN_HIKE_ID)));
                observations.add(observation);
            } while (cursor.moveToNext());
        }

        db.close();
        return observations;
    }


    // Getting Hike from DataBase
    public Hike getHike(long id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Hike.TABLE_NAME,
                new String[]{
                        Hike.COLUMN_ID,
                        Hike.COLUMN_NAME,
                        Hike.COLUMN_LOCATION,
                        Hike.COLUMN_DATE,
                        Hike.COLUMN_PARKING,
                        Hike.COLUMN_LENGTH,
                        Hike.COLUMN_DIFFICULTY,
                        Hike.COLUMN_DESCRIPTION},
                Hike.COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null,
                null);

        if (cursor !=null)
        {
            cursor.moveToFirst();
        }

        int parkingValue = cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_PARKING));
        boolean isParkingAvailable = (parkingValue == 1);

        Hike hike = new Hike(
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DATE)),
                isParkingAvailable,
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LENGTH)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_DIFFICULTY)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_ID))
        );

        cursor.close();
        return hike;
    }

    // Getting all Hikes
    public ArrayList<Hike> getAllHikes(){
        ArrayList<Hike> hikes = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Hike.TABLE_NAME + " ORDER BY " + Hike.COLUMN_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            int parkingValue = cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_PARKING));
            boolean isParkingAvailable = (parkingValue == 1);
            do {
                Hike hike = new Hike();
                hike.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_ID)));
                hike.setName(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_NAME)));
                hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LOCATION)));
                hike.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DATE)));
                hike.setParking(isParkingAvailable);
                hike.setLength(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LENGTH)));
                hike.setDifficulty(cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_DIFFICULTY)));
                hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DESCRIPTION)));
                hikes.add(hike);
            } while (cursor.moveToNext());
        }

        db.close();
        return hikes;
    }

    // insert Hike
    public long insertHike(String name, String location, String date, Boolean parking, String length, int difficulty, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Hike.COLUMN_NAME, name);
        values.put(Hike.COLUMN_LOCATION, location);
        values.put(Hike.COLUMN_DATE, date);
        values.put(Hike.COLUMN_PARKING, parking);
        values.put(Hike.COLUMN_LENGTH, length);
        values.put(Hike.COLUMN_DIFFICULTY, difficulty);
        values.put(Hike.COLUMN_DESCRIPTION, description);

        long id = db.insert(Hike.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // update Hike
    public int updateHike(Hike hike){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Hike.COLUMN_NAME, hike.getName());
        values.put(Hike.COLUMN_LOCATION, hike.getLocation());
        values.put(Hike.COLUMN_DATE, hike.getDate());
        values.put(Hike.COLUMN_PARKING, hike.getParking());
        values.put(Hike.COLUMN_LENGTH, hike.getLength());
        values.put(Hike.COLUMN_DIFFICULTY, hike.getDifficulty());
        values.put(Hike.COLUMN_DESCRIPTION, hike.getDescription());

        return db.update(Hike.TABLE_NAME, values, Hike.COLUMN_ID + "=?", new String[]{String.valueOf(hike.getId())});
    }

    // delete Hike
    public void deleteHike(Hike hike){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Hike.TABLE_NAME, Hike.COLUMN_ID + "=?", new String[]{String.valueOf(hike.getId())});
        db.close();
    }

    public void deleteAllHikes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Hike.TABLE_NAME);
        db.close();
    }
}
