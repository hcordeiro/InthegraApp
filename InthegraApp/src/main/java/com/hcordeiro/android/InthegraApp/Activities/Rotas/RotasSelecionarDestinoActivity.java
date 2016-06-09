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

/**
 * Activity para selecionar o destino de uma rota.
 *
 * Created by hugo on 17/05/16.
 */
public class RotasSelecionarDestinoActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "SelecionarDestino";

    private GoogleMap map;
    private LatLng destino;
    private Button confirmaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotas_selecionar_destino_activity);

        /* Preenche a tela */
        preencherDados();
    }

    /**
     * Preenche os dados na tela
     */
    private void preencherDados() {
        Log.d(TAG, "preencherDados Called");
         /* Recupera o destino setado no menu anterior
         * para, se existir, ser exibido no mapa */
        Bundle bundle = getIntent().getParcelableExtra("Bundle");
        destino = bundle.getParcelable("Destino");

        /* Recupera o Fragment da tela que possui um mapa */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        /* Recupera de maneira assíncrona o mapa */
        mapFragment.getMapAsync(this);

        /* Recupera o botão de confirmação e,
         * caso não haja destino selecionado,
         * o desabilita */
        confirmaBtn = (Button) findViewById(R.id.confirmaBtn);
        if (destino == null) {
            confirmaBtn.setEnabled(false);
        }
    }

    /* Seta o destino selecionado e finaliza a
     * activity, voltando para a activity anterior */
    public void confirma(View view) {
        Log.d(TAG, "confirma Called");
        if (destino != null) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("Destino", destino);
            intent.putExtra("Bundle", bundle);
            setResult(0, intent);
            finish();
        }
    }

    /**
     * Função chamada quando o carregamento do mapa é finalizado
     * @param googleMap mapa carregado
     */
    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady Called");
        map = googleMap;
        /* Se o usuário permitiu os serviços de localização,
         * o botão "meu local" é habilitado no mapa */
        if (Util.IS_LOCATION_AUTHORIZED) {
            map.setMyLocationEnabled(true);
        }

        /* Seta a funcão observará cliques longos no mapa */
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            /**
             * Quando um clique logo é detectado, o mapa é limpo, a
             * origem é atualizada, e um marcador é exibido no local
             * do clique.
             * @param latLng posição geográfica do clique.
             */
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

        /* Centraliza o mapa na cidade de Teresina */
        LatLng pos = Util.TERESINA;

        /* Se a origem carregada da Activity anterior
         * não for nula, atualiza o centro do mapa e
         * adiciona um marcador para indicar a posição. */
        if (destino != null) {

            map.addMarker(new MarkerOptions()
                    .position(destino)
                    .title("Destino"));
            pos = destino;
        }

        /* Seta a localização central do mapa e anima a camêra para a vizualização indicada */
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, Util.ZOOM);
        map.animateCamera(cameraUpdate);
    }
}
