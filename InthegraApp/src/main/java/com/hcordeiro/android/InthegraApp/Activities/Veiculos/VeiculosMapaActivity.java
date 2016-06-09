package com.hcordeiro.android.InthegraApp.Activities.Veiculos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsyncResponse;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.GoogleMaps.ItemParadaClusterizavel;
import com.hcordeiro.android.InthegraApp.Util.GoogleMaps.ParadaClusterRenderer;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de detalhe de veículos, exibe a localização dos veículos de uma linha.
 *
 * Created by hugo on 17/05/16.
 */
public class VeiculosMapaActivity extends FragmentActivity implements OnMapReadyCallback, InthegraVeiculosAsyncResponse {
    private final String TAG = "DetailVeiculos";

    private Linha linha;
    private List<Parada> paradas;
    private List<Veiculo> veiculos;
    private List<Marker> veiculosMarkers;
    private List<Marker> paradasMarkers;
    private GoogleMap map;

    private Handler UI_HANDLER = new Handler();
    private Runnable UI_UPDTAE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            carregarVeiculos();
            UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, Util.VEICULOS_REFRESH_TIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.veiculos_detail_activity);
        veiculosMarkers = new ArrayList<>();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        carregarParadas();
        carregarVeiculos();

        UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, Util.VEICULOS_REFRESH_TIME);
    }

    private void carregarParadas() {
        Log.i(TAG, "carregarParadas Called");
        linha = (Linha) getIntent().getSerializableExtra("Linha");
        veiculos = new ArrayList<>();

        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        paradas = new ArrayList<>();
        try {
            Log.d(TAG, "Carregando paradas...");
            paradas = InthegraService.getParadas(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar paradas, motivo: " + e.getMessage());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VeiculosMapaActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar recuperar a lista de Paradas da Linha informada");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
        TextView qtdParadasTxt = (TextView) findViewById(R.id.qtdParadasTxt);
        qtdParadasTxt.setText(String.valueOf(paradas.size()));
    }

    private void carregarVeiculos() {
        InthegraVeiculosAsync asyncTask =  new InthegraVeiculosAsync(VeiculosMapaActivity.this);
        asyncTask.delegate = this;
        asyncTask.execute(linha);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;
        paradasMarkers = new ArrayList<>();

        ClusterManager<ItemParadaClusterizavel> clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setRenderer(new ParadaClusterRenderer(this, googleMap, clusterManager));
        googleMap.setOnCameraChangeListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        for (Parada p : paradas) {
            LatLng pos = new LatLng(p.getLat(), p.getLong());
            MarkerOptions m = new MarkerOptions()
                    .position(pos)
                    .title(p.getCodigoParada())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.paradapointer));
            paradasMarkers.add(map.addMarker(m));
            clusterManager.addItem(new ItemParadaClusterizavel(p));
        }

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Builder builder = new Builder();
                for (Marker m : paradasMarkers) {
                    builder.include(m.getPosition());
                }
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));
            }
        });

    }

    private void updateMapa() {
        Log.i(TAG, "updateMapa Called");
        Toast.makeText(VeiculosMapaActivity.this, "Atualizando mapa...", Toast.LENGTH_SHORT).show();
        for (Marker m : veiculosMarkers) {
            m.remove();
        }
        veiculosMarkers.clear();

        for (Veiculo v : veiculos) {
            LatLng pos = new LatLng(v.getLat(), v.getLong());
            MarkerOptions m = new MarkerOptions()
                    .position(pos)
                    .title(v.getHora())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.onibuspointer));
            veiculosMarkers.add(map.addMarker(m));
        }
    }

    @Override
    public void processFinish(List<Veiculo> result) {
        Log.i(TAG, "ProcessFinish Called");
        veiculos = result;
        TextView qtdVeiculosTxt = (TextView) findViewById(R.id.qtdVeiculosTxt);
        qtdVeiculosTxt.setText(String.valueOf(veiculos.size()));
        updateMapa();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "OnDestroy Called");
        UI_HANDLER.removeCallbacks(UI_UPDTAE_RUNNABLE);
        super.onDestroy();
    }
}
