package com.example.john.ciceron;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import data.CiceronContract;
import data.CiceronDBHelper;

public class DBInfoActivity extends AppCompatActivity {


    TextView dBInfo;
    private CiceronDBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbinfo);
        dBInfo = (TextView) findViewById(R.id.textViewDBInfo);
        mDBHelper = new CiceronDBHelper(this);
        showDatabaseInfo();
    }

    private void showDatabaseInfo() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                CiceronContract.List._ID,
                CiceronContract.List.COLUMN_PLACE,
                CiceronContract.List.COLUMN_DESCRIBE };

        Cursor cursor = db.query(CiceronContract.List.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            int idColumnIndex = cursor.getColumnIndex(CiceronContract.List._ID);
            int placeColumnIndex = cursor.getColumnIndex(CiceronContract.List.COLUMN_PLACE);
            int describeColumnIndex = cursor.getColumnIndex(CiceronContract.List.COLUMN_DESCRIBE);
            dBInfo.setText("Table - " + CiceronContract.List.TABLE_NAME + "\n");
            dBInfo.append("-" + CiceronContract.List._ID + " - " + CiceronContract.List.COLUMN_PLACE + " - " + CiceronContract.List.COLUMN_DESCRIBE + "-\n");

            while (cursor.moveToNext()) {
                String id = cursor.getString(idColumnIndex);
                String place = cursor.getString(placeColumnIndex);
                String describe = cursor.getString(describeColumnIndex);
                dBInfo.append("-" + id + " - " + place + " - " + describe + "-\n");
            }
        } finally {
            cursor.close();
        }

        //show data from table tasks
        String[] projectionTask = {
                CiceronContract.Task.COLUMN_LIST_ID,
                CiceronContract.Task.COLUMN_TASK,
                CiceronContract.Task.COLUMN_DONE
        };

        Cursor cursorTask = db.query(CiceronContract.Task.TABLE_NAME,
                projectionTask,
                null,
                null,
                null,
                null,
                null);

        try {
            int idColumnIndex = cursorTask.getColumnIndex(CiceronContract.Task.COLUMN_LIST_ID);
            int taskColumnIndex = cursorTask.getColumnIndex(CiceronContract.Task.COLUMN_TASK);
            int doneColumnIndex = cursorTask.getColumnIndex(CiceronContract.Task.COLUMN_DONE);
            dBInfo.append("______________________\n");
            dBInfo.append("Table - " + CiceronContract.Task.TABLE_NAME + "\n");
            dBInfo.append("-" + CiceronContract.Task.COLUMN_LIST_ID + " - " + CiceronContract.Task.COLUMN_TASK
                    + " - " + CiceronContract.Task.COLUMN_DONE + "-\n");

            while (cursorTask.moveToNext()) {
                String list_id = cursorTask.getString(idColumnIndex);
                String task = cursorTask.getString(taskColumnIndex);
                String done = cursorTask.getString(doneColumnIndex);
                dBInfo.append("-" + list_id + " - " + task + " - " + done + "-\n");
            }
        } finally {
            cursorTask.close();
        }

        //show data from table places
        String[] projectionPlace = {
                CiceronContract.Place.COLUMN_PLACE,
                CiceronContract.Place.COLUMN_LATITUDE,
                CiceronContract.Place.COLUMN_LONGITUDE
        };

        Cursor cursorPlace = db.query(CiceronContract.Place.TABLE_NAME,
                projectionPlace,
                null,
                null,
                null,
                null,
                null);

        try {
            int placeColumnIndex = cursorPlace.getColumnIndex(CiceronContract.Place.COLUMN_PLACE);
            int latitudeColumnIndex = cursorPlace.getColumnIndex(CiceronContract.Place.COLUMN_LATITUDE);
            int longitudeColumnIndex = cursorPlace.getColumnIndex(CiceronContract.Place.COLUMN_LONGITUDE);
            dBInfo.append("______________________\n");
            dBInfo.append("Table - " + CiceronContract.Place.TABLE_NAME + "\n");
            dBInfo.append("-" + CiceronContract.Place.COLUMN_PLACE + " - " + CiceronContract.Place.COLUMN_LATITUDE
                    + " - " + CiceronContract.Place.COLUMN_LONGITUDE + "-\n");

            while (cursorPlace.moveToNext()) {
                String place = cursorPlace.getString(placeColumnIndex);
                String latitude = cursorPlace.getString(latitudeColumnIndex);
                String longitude = cursorPlace.getString(longitudeColumnIndex);
                dBInfo.append("-" + place + " - " + latitude + " - " + longitude + "-\n");
            }
        } finally {
            cursorTask.close();
        }
    }
}
