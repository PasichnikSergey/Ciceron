package com.example.john.ciceron;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import data.CiceronContract;
import data.CiceronDBHelper;

public class LocationActivity extends Activity implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap googleMap;
    Marker mark;
    EditText editPlaceName;
    private CiceronDBHelper mDBHelper;
    ArrayList places = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        editPlaceName = (EditText)findViewById(R.id.editText_place_name);
        mDBHelper = new CiceronDBHelper(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
    }

    public void setUpMap() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(mark == null) {
                    mark = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Marker")
                            .draggable(true)
                    );
                }
            }
        });

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = lm.getBestProvider(new Criteria(), true);

        Location loc = lm.getLastKnownLocation(provider);

        if (loc == null) {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    LatLng ll = new LatLng(lat, lng);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
                }
            };
            lm.requestLocationUpdates(provider, 20000, 1, (android.location.LocationListener) locationListener);
        }
        if(loc != null) {
            onLocationChanged(loc);
        }
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker = mark;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latlngPos = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngPos, 15));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
    }

    public void onClickSaveLocation(View view) {
        getInfoForPlaces();
        String placeName = editPlaceName.getText().toString();
        if(placeName.length() < 1) {
            showAlert("Input place name!!!");
        } else if (null==mark){
            showAlert("Long tap on map to add place!");
        } else {
            String place = editPlaceName.getText().toString();
            String latitude = mark.getPosition().latitude + "";
            String longitude = mark.getPosition().longitude + "";
            if(places.contains(place)) {
                showAlert("Same place is in DB");
            } else {
                insertDataToDB(place, latitude, longitude);
                Intent intent_save_location = new Intent(LocationActivity.this, TaskActivity.class);
                startActivity(intent_save_location);
            }

        }
    }

    private void insertDataToDB(String place, String latitude, String longitude) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values_places = new ContentValues();
        values_places.put(CiceronContract.Place.COLUMN_PLACE, place);
        values_places.put(CiceronContract.Place.COLUMN_LATITUDE, latitude);
        values_places.put(CiceronContract.Place.COLUMN_LONGITUDE, longitude);

        long newRowIdPlace = db.insert(CiceronContract.Place.TABLE_NAME, null, values_places);
    }

    public void showAlert(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationActivity.this);

        alertDialog.setTitle("WARNING!!!");
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Код который выполнится после закрытия окна
                editPlaceName.requestFocus();
            }
        });
        AlertDialog alert = alertDialog.create();

        // показываем Alert
        alert.show();
    }

    public void getInfoForPlaces() {
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
    }

}
