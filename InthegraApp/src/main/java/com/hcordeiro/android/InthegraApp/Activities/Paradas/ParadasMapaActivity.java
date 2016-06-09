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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

/**
 * Activity de mapa de parada, exibe um mapa com a parada selecionada
 *
 * Created by hugo on 17/05/16.
 */
@SuppressWarnings("MissingPermission")
public class ParadasMapaActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "ParadaMadas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paradas_mapa_activity);
        /* Se o usuário não possuir conexão com a internet, a activity é finalizada */
        if (!Util.isOnline(this)) {
            finish();
        } else {
            /* Preenche a tela dos dados da linha */
            preencherDados();

            /* Recupera o Fragment da tela que possui um mapa */
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            /* Recupera de maneira assíncrona o mapa */
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Preeche os dados na tela
     */
    private void preencherDados() {
        Log.d(TAG, "preencherDados Called");
        /* Recupera a parada selecionada no menu anterior e,
         * recupera e preenche o TextView da denominação da parada */
        Parada parada = (Parada) getIntent().getSerializableExtra("Parada");
        TextView denominacaoParadaTxt = (TextView) findViewById(R.id.denominacaoParadaTxt);
        denominacaoParadaTxt.setText(parada.getDenomicao());
    }

    /**
     * Função chamada quando o carregamento do mapa é finalizado
     * @param googleMap mapa carregado
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        /* Se o usuário permitiu os serviços de localização,
         * o botão "meu local" é habilitado no mapa */
        if (Util.IS_LOCATION_AUTHORIZED) {
            googleMap.setMyLocationEnabled(true);
        }

        /* Recupera a parada selecionada no menu anterior */
        Parada parada = (Parada) getIntent().getSerializableExtra("Parada");
        LatLng pos = new LatLng(parada.getLat(), parada.getLong());
        googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(parada.getCodigoParada())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.paradapointer)));

        /* Seta a localização da parada e anima a camêra para a vizualização indicada */
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 15);
        googleMap.animateCamera(cameraUpdate);
    }
}
