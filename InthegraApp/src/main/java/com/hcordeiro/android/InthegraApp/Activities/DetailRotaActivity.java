package com.hcordeiro.android.InthegraApp.Activities;

import android.os.Bundle;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraDirectionsAsync;
import com.hcordeiro.android.InthegraApp.R;

import java.util.List;

public class DetailRotaActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "DetailRota";

    private GoogleMap map;
    private Rota rota;
    LatLngBounds bounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rota);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rota = (Rota) getIntent().getSerializableExtra("Rota");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
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

        bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 800, 800, 1);
        map.animateCamera(cameraUpdate);
    }

    private void getDirections(Trecho trecho) {
        Log.i(TAG, "GetDirections Called");
        InthegraDirectionsAsync asyncTask =  new InthegraDirectionsAsync(DetailRotaActivity.this, map);
        asyncTask.execute(trecho);
    }
}
