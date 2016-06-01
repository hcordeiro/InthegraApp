package com.hcordeiro.android.InthegraApp.Activities.Linhas;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.GoogleMaps.ItemParadaClusterizavel;
import com.hcordeiro.android.InthegraApp.Util.GoogleMaps.ParadaClusterRenderer;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("MissingPermission")
public class LinhasMapaActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "LinhasMapa";
    private List<Parada> paradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linhas_mapa_activity);
        if (!Util.isOnline(this)) {
            finish();
        } else {
            preencherDados();
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        String paradasJson = getIntent().getStringExtra("Paradas");
        paradas = new ArrayList<>(Arrays.asList(new Gson().fromJson(paradasJson, Parada[].class)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        if (Util.IS_LOCATION_AUTHORIZED) {
            googleMap.setMyLocationEnabled(true);
        }

        ClusterManager<ItemParadaClusterizavel> clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setRenderer(new ParadaClusterRenderer(this, googleMap, clusterManager));
        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        Builder builder = new Builder();
        for (Parada parada : paradas) {
            MarkerOptions marcador = new MarkerOptions()
                    .position(new LatLng(parada.getLat(), parada.getLong()))
                    .title(parada.getEndereco())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.paradapointer));

            builder.include(marcador.getPosition());
            clusterManager.addItem(new ItemParadaClusterizavel(parada));
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 1);
        googleMap.animateCamera(cameraUpdate);
    }
}