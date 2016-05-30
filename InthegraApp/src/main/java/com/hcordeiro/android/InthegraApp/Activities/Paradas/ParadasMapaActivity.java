package com.hcordeiro.android.InthegraApp.Activities.Paradas;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.equalsp.stransthe.Parada;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

@SuppressWarnings("MissingPermission")
public class ParadasMapaActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "ParadaMadas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paradas_mapa_activity);
        if (!Util.isOnline(this)) {
            finish();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        preencherDados();
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
        Parada parada = (Parada) getIntent().getSerializableExtra("Parada");
        TextView denominacaoParadaTxt = (TextView) findViewById(R.id.denominacaoParadaTxt);
        denominacaoParadaTxt.setText(parada.getDenomicao());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        GoogleMap map = googleMap;
        Parada parada = (Parada) getIntent().getSerializableExtra("Parada");
        LatLng pos = new LatLng(parada.getLat(), parada.getLong());
        map.addMarker(new MarkerOptions()
                .position(pos)
                .title(parada.getCodigoParada())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.paradapointer)));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 15);
        map.animateCamera(cameraUpdate);
        if (Util.IS_LOCATION_AUTHORIZED) {
            map.setMyLocationEnabled(true);
        }
    }
}
