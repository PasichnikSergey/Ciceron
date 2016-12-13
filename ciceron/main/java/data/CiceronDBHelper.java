package data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by John on 07.12.2016.
 */

public class CiceronDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CiceronContract.class.getSimpleName();

    private static final String DATABASE_NAME = "ciceron.db";

    private static final int DATABASE_VERSION = 1;

    public CiceronDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    public CiceronDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CiceronDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_LISTMAIN_TABLE = "CREATE TABLE " + CiceronContract.List.TABLE_NAME + " ("
                + CiceronContract.List._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CiceronContract.List.COLUMN_PLACE + " TEXT NOT NULL, "
                + CiceronContract.List.COLUMN_DESCRIBE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_LISTMAIN_TABLE);

        String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + CiceronContract.Task.TABLE_NAME + " ("
                + CiceronContract.Task._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CiceronContract.Task.COLUMN_LIST_ID + " INTEGER, "
                + CiceronContract.Task.COLUMN_TASK + " TEXT NOT NULL, "
                + CiceronContract.Task.COLUMN_DONE + " INTEGER DEFAULT 0);";
        db.execSQL(SQL_CREATE_TASKS_TABLE);

        String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + CiceronContract.Place.TABLE_NAME + " ("
                + CiceronContract.Place._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CiceronContract.Place.COLUMN_PLACE + " TEXT NOT NULL, "
                + CiceronContract.Place.COLUMN_LATITUDE + " TEXT NOT NULL, "
                + CiceronContract.Place.COLUMN_LONGITUDE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_PLACES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.w("SQLite", "Upgrade database from version " + i + " to version " + i1);

        db.execSQL("DROP TABLE IF IT EXIST " + DATABASE_NAME);

        onCreate(db);
    }
}
