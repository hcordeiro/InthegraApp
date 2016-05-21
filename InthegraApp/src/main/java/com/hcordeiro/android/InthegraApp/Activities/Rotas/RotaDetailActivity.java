package com.hcordeiro.android.InthegraApp.Activities.Rotas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.equalsp.stransthe.Localizacao;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.rotas.Rota;
import com.equalsp.stransthe.rotas.Trecho;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraDirectionsAsync;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.util.List;

public class RotaDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "DetailRota";
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rota);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;
        final int LOCATION_REFRESH_TIME = 1000;
        final int LOCATION_REFRESH_DISTANCE = 5;
        LatLngBounds bounds = adicionarMarcadores();
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_ACCESS_LOCATION = 1;
            String[] PERMISSIONS_LOCATION = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            ActivityCompat.requestPermissions(RotaDetailActivity.this, PERMISSIONS_LOCATION, REQUEST_ACCESS_LOCATION);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
        map.setMyLocationEnabled(true);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 800, 800, 1);
        map.animateCamera(cameraUpdate);
    }

    private LatLngBounds adicionarMarcadores() {
        Log.i(TAG, "adicionarMarcadores Called");
        Rota rota = (Rota) getIntent().getSerializableExtra("Rota");
        Builder builder = new Builder();
        List<Trecho> trechos = rota.getTrechos();
        int trechosAPe = 0;

        for (Trecho trecho : trechos) {
            if (trecho.getLinha() == null) {
                trechosAPe = trechosAPe + 1;
                getDirections(trecho);
            }

            Localizacao origem = trecho.getOrigem();
            String tituloOrigem = "Origem do trecho a pé " + trechosAPe;
            if (origem instanceof Parada) {
                Parada p = (Parada) origem;
                tituloOrigem = p.getDenomicao();
            }

            MarkerOptions mOrigem = new MarkerOptions()
                    .position(new LatLng(origem.getLat(), origem.getLong()))
                    .title(tituloOrigem);

            map.addMarker(mOrigem);

            builder.include(mOrigem.getPosition());
        }

        Trecho ultimoTrecho = trechos.get(trechos.size()-1);
        MarkerOptions destinoFinal = new MarkerOptions()
                .position(new LatLng(ultimoTrecho.getDestino().getLat(), ultimoTrecho.getDestino().getLong()))
                .title("Destino Final");
        map.addMarker(destinoFinal);

        return builder.build();
    }

    private void getDirections(Trecho trecho) {
        Log.i(TAG, "GetDirections Called");
        InthegraDirectionsAsync asyncTask =  new InthegraDirectionsAsync(RotaDetailActivity.this, map);
        asyncTask.execute(trecho);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, Util.ZOOM);
            map.animateCamera(cameraUpdate);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled");
        }
    };
}
