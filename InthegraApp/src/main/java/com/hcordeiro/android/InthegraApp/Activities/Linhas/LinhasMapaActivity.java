package com.hcordeiro.android.InthegraApp.Activities.Linhas;

import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.rotas.PontoDeInteresse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("MissingPermission")
public class LinhasMapaActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "LinhasMapa";
    private GoogleMap map;
    private Location localUsuario;
    List<Marker> listaMarcadores;
    List<Parada> paradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linhas_mapa_activity);

        if (!Util.isOnline(this)) {
            finish();
        } else {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            Util.requestLocation(this, mLocationListener);
            preencherDados();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;
        carregarMarcadores();
        if (Util.IS_LOCATION_AUTHORIZED) {
            map.setMyLocationEnabled(true);
        }
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        String paradasJson = getIntent().getStringExtra("Paradas");
        paradas = new ArrayList<>(Arrays.asList(new Gson().fromJson(paradasJson, Parada[].class)));
        listaMarcadores = new ArrayList<>();
    }

    private void carregarMarcadores() {
        Log.i(TAG, "carregarMarcadores Called");
        map.clear();
        listaMarcadores.clear();
        Builder builder = new Builder();

        PontoDeInteresse pontoDeInteresse = null;
        if (localUsuario != null) {
            pontoDeInteresse = new PontoDeInteresse(localUsuario.getLatitude(), localUsuario.getLongitude());
            builder.include(new LatLng(localUsuario.getLatitude(), localUsuario.getLongitude()));
        }

        for (Parada parada : paradas) {
            MarkerOptions marcador = new MarkerOptions()
                    .position(new LatLng(parada.getLat(), parada.getLong()))
                    .title(parada.getEndereco());
            listaMarcadores.add(map.addMarker(marcador));

            if (pontoDeInteresse != null) {
                if (parada.getDistancia(pontoDeInteresse) < Util.DISTANCIA_MAXIMA_A_PE) {
                    builder.include(new LatLng(parada.getLat(), parada.getLong()));
                }
            } else {
                builder.include(marcador.getPosition());
            }
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 1);
        map.animateCamera(cameraUpdate);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Toast.makeText(LinhasMapaActivity.this, "Atualizando localização...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            localUsuario = location;
            carregarMarcadores();
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
