package com.hcordeiro.android.InthegraApp.Util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;

/**
 * Contantes e métodos utilitários
 *
 * Created by hugo on 17/05/16.
 */
public class Util {
    private static final String TAG = "Util";

    public static final LatLng TERESINA = new LatLng(-5.070353, -42.803180);
    public static final int ZOOM = 15;
    public static final double DISTANCIA_MAXIMA_A_PE = 250;
    public static final String ERRO_API_404 = "Not Found (404)";
    public static final int VEICULOS_REFRESH_TIME = 30000;

    private static final int LOCATION_REFRESH_TIME = 10000;
    private static final int LOCATION_REFRESH_DISTANCE = 10;

    public static void requestLocation(Activity activity) {
        requestLocation(activity, null, null);
    }

    public static void requestLocation(Activity activity, LocationManager locationManager, LocationListener locationListener) {
        Log.i(TAG, "requestLocation Called");

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            int REQUEST_ACCESS_LOCATION = 1;
            String[] PERMISSIONS_LOCATION = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_ACCESS_LOCATION);
        }
        if (locationManager != null && locationListener != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Util.LOCATION_REFRESH_TIME, Util.LOCATION_REFRESH_DISTANCE, locationListener);
        }
    }

}
