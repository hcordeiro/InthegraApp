package com.hcordeiro.android.InthegraApp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

public class SelecionarDestinoActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "SelecionarDestino";

    private GoogleMap map;
    private LatLng destino;
    private Button confirmaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_destino);

        Bundle bundle = getIntent().getParcelableExtra("Bundle");
        destino = bundle.getParcelable("Destino");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        confirmaBtn = (Button) findViewById(R.id.confirmaBtn);
        confirmaBtn.setEnabled(false);
    }

    public void confirma(View view) {
        Log.i(TAG, "confirma Called");
        if (destino != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("Destino", destino);
            intent.putExtra("Bundle", bundle);
            setResult(0, intent);
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_ACCESS_LOCATION = 1;
            String[] PERMISSIONS_LOCATION = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            ActivityCompat.requestPermissions(SelecionarDestinoActivity.this, PERMISSIONS_LOCATION, REQUEST_ACCESS_LOCATION);
        }

        map.setMyLocationEnabled(true);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();
                destino = latLng;
                confirmaBtn.setEnabled(true);
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Destino"));
            }
        });

        LatLng pos = Util.TERESINA;
        if (destino != null) {
            map.addMarker(new MarkerOptions()
                    .position(destino)
                    .title("Destino"));
            pos = destino;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, Util.ZOOM);
        map.animateCamera(cameraUpdate);
    }
}
