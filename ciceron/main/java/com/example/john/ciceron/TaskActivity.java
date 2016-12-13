package com.example.john.ciceron;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import data.CiceronContract;
import data.CiceronDBHelper;

public class TaskActivity extends AppCompatActivity {

    private Spinner mSpinnerPlaces;
    private EditText mEditTask;
    String spinnerSelectedPosition;
    String taskToSave;

    ArrayList places = new ArrayList();
    private CiceronDBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mEditTask = (EditText)findViewById(R.id.editTask);
        mSpinnerPlaces = (Spinner) findViewById(R.id.spinner_places);
        mDBHelper = new CiceronDBHelper(this);
        getInfoForSpinner();
        setupSpinner();
    }

    public void getInfoForSpinner() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                CiceronContract.Place.COLUMN_PLACE
        };

        Cursor cursor = db.query(CiceronContract.Place.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            int placeColumnIndex = cursor.getColumnIndex(CiceronContract.Place.COLUMN_PLACE);

            while (cursor.moveToNext()) {
                places.add(cursor.getString(placeColumnIndex));
            }
        } finally {
            cursor.close();
        }
        db.close();
    }

    public void setupSpinner() {
        ArrayList places1 = new ArrayList();
        places1.add("Some place");
        ArrayAdapter placeSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, places);
        placeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinnerPlaces.setAdapter(placeSpinnerAdapter);
        mSpinnerPlaces.setSelection(0);
        mSpinnerPlaces.setPrompt("Title");
        mSpinnerPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getBaseContext(), "Position = " + i, Toast.LENGTH_SHORT);
               spinnerSelectedPosition = mSpinnerPlaces.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void onClickSave(View view) {
        taskToSave = mEditTask.getText().toString();
        if(taskToSave.length()<1){
            showAlert("You don'd input any tasks");
        } else if (spinnerSelectedPosition.equals("No selected places")) {
            showAlert("Select some place!");
        } else {
            insertDataToDB(spinnerSelectedPosition, taskToSave);

            Intent intent = new Intent(TaskActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onClickLocation(View view) {
        Intent intent_location = new Intent(TaskActivity.this, LocationActivity.class);
        startActivity(intent_location);
    }

    public void showAlert(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskActivity.this);

        alertDialog.setTitle("WARNING!!!");
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Код который выполнится после закрытия окна
                mEditTask.requestFocus();
            }
        });
        AlertDialog alert = alertDialog.create();

        // показываем Alert
        alert.show();
    }

    private void insertDataToDB(String place, String task) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        if (checkPlaceInMainList(place)) {
            //update data in task table for existing place
            int idInMainList = getIdInMainList(place);
            ContentValues values_task = new ContentValues();

            values_task.put(CiceronContract.Task.COLUMN_LIST_ID, idInMainList);
            values_task.put(CiceronContract.Task.COLUMN_TASK, task);

            long newRowIdTask = db.insert(CiceronContract.Task.TABLE_NAME, null, values_task);

        } else {
            //insert data to list table and task table
            ContentValues values_list = new ContentValues();
            values_list.put(CiceronContract.List.COLUMN_PLACE, place);
            values_list.put(CiceronContract.List.COLUMN_DESCRIBE, "Some Describe");

            long newRowIdList = db.insert(CiceronContract.List.TABLE_NAME, null, values_list);

            long lastIdInTableList = getLastInsertId();

            ContentValues values_task = new ContentValues();

            values_task.put(CiceronContract.Task.COLUMN_LIST_ID, lastIdInTableList);
            values_task.put(CiceronContract.Task.COLUMN_TASK, task);

            long newRowIdTask = db.insert(CiceronContract.Task.TABLE_NAME, null, values_task);

            //start new service for new task
            //get LatLng for Place
            LatLng latLngForService = getLatLngFromPlacesTable(place);
            startService(new Intent(this, CiceronService.class).putExtra("latitude", latLngForService.latitude)
                                                                .putExtra("longitude", latLngForService.longitude)
                                                                .putExtra("idFromListTable", lastIdInTableList+"")
                                                                .putExtra("place", place));
        }
    }

    private LatLng getLatLngFromPlacesTable(String place) {
        LatLng latLng;
        String latitude = "0";
        String longitude = "0";
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {CiceronContract.Place.COLUMN_LATITUDE, CiceronContract.Place.COLUMN_LONGITUDE};
        String selection = CiceronContract.Place.COLUMN_PLACE + "=?";
        String[] selectionArgs = {"" + place};
        Cursor cursor = db.query(
                CiceronContract.Place.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            latitude = cursor.getString(cursor.getColumnIndex(CiceronContract.Place.COLUMN_LATITUDE));
            longitude = cursor.getString(cursor.getColumnIndex(CiceronContract.Place.COLUMN_LONGITUDE));
        }
        latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        return latLng;

    }

    private String getOldTask(int idInMainList) {
        String oldTask = "";
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {CiceronContract.Task.COLUMN_TASK};
        String selection = CiceronContract.Task.COLUMN_LIST_ID + "=?";
        String[] selectionArgs = {"" + idInMainList};
        Cursor cursor = db.query(
                CiceronContract.Task.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            oldTask = cursor.getString(cursor.getColumnIndex(CiceronContract.Task.COLUMN_TASK));
        }

        return oldTask;
    }

    private void updateDataInTaskTable(int list_ID, String task) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values_task = new ContentValues();

        values_task.put(CiceronContract.Task.COLUMN_TASK, task);

        String where = CiceronContract.Task.COLUMN_LIST_ID +"=?";
        String[] whereValue = {""+ list_ID};
        long newRowIdTask = db.update(CiceronContract.Task.TABLE_NAME, values_task, where, whereValue);


    }

    private int getIdInMainList(String place) {
        ArrayList placesInMainList = getPlacesFromList();
        return 1+placesInMainList.indexOf(place);
    }

    public long getLastInsertId() {

        long index = 1;
        SQLiteDatabase sdb = mDBHelper.getReadableDatabase();
        Cursor cursor = sdb.query(
                "sqlite_sequence",
                new String[]{"seq"},
                "name = ?",
                new String[]{CiceronContract.List.TABLE_NAME},
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst ()){
            index = cursor.getLong(cursor.getColumnIndex("seq"));
        }
        cursor.close();
        return index;
    }

    public boolean checkPlaceInMainList (String place) {

        ArrayList placesInMainList = getPlacesFromList();

        if(placesInMainList.contains(place)) {
            return true;
        }
        return false;

    }

    public ArrayList getPlacesFromList () {
        ArrayList places = new ArrayList();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                CiceronContract.List.COLUMN_PLACE
        };

        Cursor cursor = db.query(CiceronContract.List.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            int placeColumnIndex = cursor.getColumnIndex(CiceronContract.List.COLUMN_PLACE);

            while (cursor.moveToNext()) {
                places.add(cursor.getString(placeColumnIndex));
            }
        } finally {
            cursor.close();
        }
        return places;
    }
}