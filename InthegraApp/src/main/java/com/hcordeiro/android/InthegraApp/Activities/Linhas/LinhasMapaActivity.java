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

/**
 * Activity de mapa de linha, exibe um mapa com o percruso da linha e suas paradas
 *
 * Created by hugo on 17/05/16.
 */
@SuppressWarnings("MissingPermission")
public class LinhasMapaActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = "LinhasMapa";
    private List<Parada> paradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linhas_mapa_activity);

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
        /* Recupera a linha selecionada no menu anterior e,
         * recupera e preenche o TextView da denominação da linha */
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        /* Recupera as paradas carregadas no menu anterior */
        String paradasJson = getIntent().getStringExtra("Paradas");
        paradas = new ArrayList<>(Arrays.asList(new Gson().fromJson(paradasJson, Parada[].class)));
    }

    /**
     * Função chamada quando o carregamento do mapa é finalizado
     * @param googleMap mapa carregado
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady Called");
        /* Se o usuário permitiu os serviços de localização,
         * o botão "meu local" é habilitado no mapa */
        if (Util.IS_LOCATION_AUTHORIZED) {
            googleMap.setMyLocationEnabled(true);
        }

        /* Cria e seta um CluterManager responsável por agrupar os marcadores
         * de paradas exibidos no mapa de acordo com o zoom da tela,
         * e um ParadaClusterRenderer responsável por exibir os agrupamentos
         * realizados no mapa. */
        ClusterManager<ItemParadaClusterizavel> clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setRenderer(new ParadaClusterRenderer(this, googleMap, clusterManager));
        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        /* Builder dos limites da tela para que o mapa exiba o percurso completo na tela */
        Builder builder = new Builder();
        for (Parada parada : paradas) {
            /* Marcador do google maps */
            MarkerOptions marcador = new MarkerOptions()
                    .position(new LatLng(parada.getLat(), parada.getLong()))
                    .title(parada.getEndereco())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.paradapointer));

            builder.include(marcador.getPosition());
            clusterManager.addItem(new ItemParadaClusterizavel(parada));
        }

        /* Seta os limites criados pelo builder e anima a camêra para a vizualização indicada */
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 1);
        googleMap.animateCamera(cameraUpdate);
    }
}