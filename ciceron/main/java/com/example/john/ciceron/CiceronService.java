package com.example.john.ciceron;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CiceronService extends Service implements LocationListener {
    public CiceronService() {
    }

    final String LOG_TAG = "myLogs";
    LatLng imHere;
    Double latitude, longitude;
    String placeFromTask;
    String idFromTask;
    ExecutorService es;

    public void onCreate() {
        super.onCreate();
        es = Executors.newFixedThreadPool(2);
        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOG_TAG, "onStartCommand");

        latitude = intent.getExtras().getDouble("latitude");
        longitude = intent.getExtras().getDouble("longitude");
        placeFromTask = intent.getStringExtra("place");
        idFromTask = intent.getStringExtra("idFromListTable");

        MyRun mr = new MyRun(latitude, longitude, placeFromTask, idFromTask, startId);
        es.execute(mr);
        //checkPosition();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    public LatLng getCurrentPosition() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = lm.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        Location loc = lm.getLastKnownLocation(provider);

        if (loc == null) {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
//                    Log.d(LOG_TAG, "override" + lat + " - " + lng);
                    imHere = new LatLng(lat, lng);
                    //check if is near place
                }
            };
            lm.requestLocationUpdates(provider, 20000, 0, (android.location.LocationListener) locationListener);
        }
        if(loc != null) {
            onLocationChanged(loc);
        }
        return imHere;
    }

    public void onLocationChanged(Location location) {
//        Log.d(LOG_TAG, "override" + location.getLatitude() + " - " + location.getLongitude());
        imHere = new LatLng(location.getLatitude(), location.getLongitude());
        //check if is near place
    }

    public void checkPosition() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while(true) {

                    imHere = getCurrentPosition();
                    i++;
                    Log.d(LOG_TAG, "Iteration = " + i);
                    Log.d(LOG_TAG, "Current latitude = " + imHere.latitude);
                    Log.d(LOG_TAG, "Task latitude = " + latitude);
                    Log.d(LOG_TAG, "Current longitude = " + imHere.longitude);
                    Log.d(LOG_TAG, "Task longitude = " + longitude);
                    Log.d(LOG_TAG, "id = " + idFromTask);

                    try {
                        java.util.concurrent.TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(Math.abs(imHere.latitude-latitude) < 0.001 || Math.abs(imHere.longitude-longitude) < 0.001) {
                        showMessage(placeFromTask, idFromTask);
                        break;
                    }
                }
                stopSelf();
            }
        }).start();
    }

    private void showMessage(String place, String list_id) {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, ViewTaskActivity.class).putExtra("place", place).putExtra("id", idFromTask);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder  = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setTicker(""+place)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Ciceron alert")
                .setContentText(place)
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.sound = ringUri;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    class MyRun implements Runnable {
        double latitude;
        double longitude;
        String place;
        String id;
        int startId;

        public MyRun(double latitude, double longitude, String place, String id, int startId) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.place = place;
            this.id = id;
            this.startId = startId;
        }
        @Override
        public void run() {
            int i = 0;
            while(true) {

                imHere = getCurrentPosition();
                i++;
                Log.d(LOG_TAG, "Iteration = " + i);
                Log.d(LOG_TAG, "Current latitude = " + imHere.latitude);
                Log.d(LOG_TAG, "Task latitude = " + latitude);
                Log.d(LOG_TAG, "Current longitude = " + imHere.longitude);
                Log.d(LOG_TAG, "Task longitude = " + longitude);
                Log.d(LOG_TAG, "Place = " + place);
                Log.d(LOG_TAG, "id = " + idFromTask);
                Log.d(LOG_TAG, "startId = " + startId);

                try {
                    java.util.concurrent.TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(Math.abs(imHere.latitude-latitude) < 0.001 || Math.abs(imHere.longitude-longitude) < 0.001) {
                    showMessage(placeFromTask, idFromTask);
                    break;
                }
            }
            stopSelf(startId);
        }

    }
}
