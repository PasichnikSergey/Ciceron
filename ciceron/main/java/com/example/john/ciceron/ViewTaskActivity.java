package com.example.john.ciceron;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import data.CiceronContract;
import data.CiceronDBHelper;

public class ViewTaskActivity extends AppCompatActivity {

    String placeFromListTable;
    int idFromListTable;
    private ArrayAdapter<String> arrayAdapterTasks;
    private CiceronDBHelper mDBHelper;
    private ArrayList tasksArray = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        TextView infoPosition = (TextView) findViewById(R.id.textViewPosition);

        //get place and his _id from mainActivity
        placeFromListTable = getIntent().getExtras().getString("place");
        idFromListTable = Integer.parseInt(getIntent().getExtras().getString("id"));

        //Show place
        infoPosition.setText("" + placeFromListTable);

        //get data of Task table for arrayAdapter
        mDBHelper = new CiceronDBHelper(this);
        getTaskByList_id(idFromListTable);

        //filling list
        ListView listOfTasks = (ListView) findViewById(R.id.listViewTasks);
        arrayAdapterTasks = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tasksArray);
        listOfTasks.setAdapter(arrayAdapterTasks);

    }
    private void getTaskByList_id(int idInMainList) {
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
            tasksArray.add(cursor.getString(cursor.getColumnIndex(CiceronContract.Task.COLUMN_TASK)));
        }
        cursor.close();
    }
}


