package com.example.john.ciceron;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ListActivity;

import java.util.ArrayList;

import data.CiceronDBHelper;
import data.CiceronContract.*;


public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {

    private CiceronDBHelper mDBHelper;
    ArrayList places = new ArrayList();
    ArrayList describes = new ArrayList();
    ArrayList ids = new ArrayList();
    private ArrayAdapter<String> mainListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                startActivity(intent);
            }
        });
        mDBHelper = new CiceronDBHelper(this);
        //clearDB();
        getDatabaseInfo();
        ListView listView = (ListView)findViewById(R.id.mainList);
        mainListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(mainListAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            clearDB();
            return true;
        }

        if (id == R.id.action_init) {
            insertData();
            return true;
        }

        if (id == R.id.action_show_DB) {
            Intent intentDBInfo = new Intent(MainActivity.this, DBInfoActivity.class);
            startActivity(intentDBInfo);
            return true;
        }

        if (id == R.id.action_StartService) {
            startService(new Intent(this, CiceronService.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDatabaseInfo() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                List._ID,
                List.COLUMN_PLACE,
                List.COLUMN_DESCRIBE };

        Cursor cursor = db.query(List.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            int id = cursor.getColumnIndex(List._ID);
            int placeColumnIndex = cursor.getColumnIndex(List.COLUMN_PLACE);
            int describeColumnIndex = cursor.getColumnIndex(List.COLUMN_DESCRIBE);

            while (cursor.moveToNext()) {
                ids.add(cursor.getString(id));
                places.add(cursor.getString(placeColumnIndex));
                describes.add(cursor.getString(describeColumnIndex));
            }
        } finally {
            cursor.close();
        }
    }

    private void insertData() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values_list = new ContentValues();
        values_list.put(List.COLUMN_PLACE, "No places");
        values_list.put(List.COLUMN_DESCRIBE, "No describes");

        long newRowIdList = db.insert(List.TABLE_NAME, null, values_list);

        ContentValues values_tasks = new ContentValues();
        values_tasks.put(Task.COLUMN_TASK, "No tasks");
        values_tasks.put(Task.COLUMN_DONE, "0");

        long newRowIdTask = db.insert(Task.TABLE_NAME, null, values_tasks);

        ContentValues values_places = new ContentValues();
        values_places.put(Place.COLUMN_PLACE, "No selected places");
        values_places.put(Place.COLUMN_LATITUDE, "0");
        values_places.put(Place.COLUMN_LONGITUDE, "0");

        long newRowIdPlace = db.insert(Place.TABLE_NAME, null, values_places);
    }

    private void clearDB() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.delete(List.TABLE_NAME, null, null);
        db.delete(Task.TABLE_NAME, null, null);
        db.delete(Place.TABLE_NAME, null, null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent_view_task = new Intent(MainActivity.this, ViewTaskActivity.class);
        intent_view_task.putExtra("place", places.get(i).toString());
        intent_view_task.putExtra("id", ids.get(i).toString());
        startActivity(intent_view_task);
    }
}

