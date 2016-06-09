package com.hcordeiro.android.InthegraApp.Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Contantes e métodos utilitários
 *
 * Created by hugo on 17/05/16.
 */
public class Util {
    private static final String TAG = "Util";

    /*
    Permissions
     */
    public static final int REQUEST_ACCESS_LOCATION = 1;
    public static boolean IS_LOCATION_AUTHORIZED;

    public static final int ZOOM = 15;
    public static final int VEICULOS_REFRESH_TIME = 30000;
    public static final double DISTANCIA_MAXIMA_A_PE = 250;
    public static final LatLng TERESINA = new LatLng(-5.070353, -42.803180);

    private static final int LOCATION_REFRESH_TIME = 10000;
    private static final int LOCATION_REFRESH_DISTANCE = 10;


//    public static final String ERRO_API_404 = "Not Found (404)";
//    public static final String ERRO_HOSTNAME = "Unable to resolve host \"api.inthegra.strans.teresina.pi.gov.br\": No address associated with hostname";

    @SuppressWarnings("MissingPermission")
    public static void requestLocation(Context context, LocationListener locationListener) {
        Log.d(TAG, "requestLocation Called");
        if (IS_LOCATION_AUTHORIZED) {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager != null && locationListener != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Util.LOCATION_REFRESH_TIME, Util.LOCATION_REFRESH_DISTANCE, locationListener);
            }
        }
    }

    public static void checarPermissoes(Activity activity) {
        Log.d(TAG, "checarPermissoes Called");
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] PERMISSIONS_LOCATION = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_ACCESS_LOCATION);
        } else {
            IS_LOCATION_AUTHORIZED = true;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}
