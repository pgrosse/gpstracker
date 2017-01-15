package de.pgr.android.gpstest;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;


public class GPSService extends Service implements LocationListener {

    public static List<Location> weg;
    public static TextViewHandler handler;
    private LocationManager manager;


    public GPSService() {
        super();
        weg = new ArrayList<>();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        weg.clear();
        handler.sendEmptyMessage(0);

        if( manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            requestSingleUpdate();
        }

        if( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            requestSingleUpdate();
        }

        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

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

        handler.sendEmptyMessage(0);
        manager.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {

        weg.add( location );

        if (handler != null) {
            Message msg = new Message();

            msg.what = weg.size();

            msg.setTarget(handler);
            msg.sendToTarget();
//            handler.sendMessage( msg );
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }


    @Override
    public void onProviderEnabled(String s) {
        requestSingleUpdate();
    }


    @Override
    public void onProviderDisabled(String s) {

    }


    private void requestSingleUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        manager.requestSingleUpdate( LocationManager.NETWORK_PROVIDER, this, null );
        manager.requestSingleUpdate( LocationManager.GPS_PROVIDER, this, null );
    }

}
