package com.hcordeiro.android.InthegraApp.Activities.Rotas;

import android.content.Intent;
import android.os.Bundle;
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

public class RotasSelecionarOrigemActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "SelecionarOrigem";

    private GoogleMap map;
    private LatLng origem;
    private Button confirmaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotas_selecionar_origem_activity);
        Util.requestLocation(this);
        preencherDados();
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
        Bundle bundle = getIntent().getParcelableExtra("Bundle");
        origem = bundle.getParcelable("Origem");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        confirmaBtn = (Button) findViewById(R.id.confirmaBtn);
        if (origem == null) {
            confirmaBtn.setEnabled(false);
        }
    }

    public void confirma(View view) {
        Log.i(TAG, "confirma Called");
        if (origem != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("Origem", origem);
            intent.putExtra("Bundle", bundle);
            setResult(0, intent);
            finish();
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;
        map.setMyLocationEnabled(true);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.clear();
                origem = latLng;
                confirmaBtn.setEnabled(true);
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Origem"));
            }
        });

        LatLng pos = Util.TERESINA;
        if (origem != null) {
            map.addMarker(new MarkerOptions()
                    .position(origem)
                    .title("Origem"));
            pos = origem;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, Util.ZOOM);
        map.animateCamera(cameraUpdate);
    }
}