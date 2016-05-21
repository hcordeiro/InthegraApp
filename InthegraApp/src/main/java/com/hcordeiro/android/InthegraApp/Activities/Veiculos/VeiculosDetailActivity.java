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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsyncResponse;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de detalhe de veículos, exibe a localização dos veículos de uma linha.
 *
 * Created by hugo on 17/05/16.
 */
public class VeiculosDetailActivity extends FragmentActivity implements OnMapReadyCallback, InthegraVeiculosAsyncResponse {
    private final String TAG = "DetailVeiculos";

    private Linha linha;
    private List<Parada> paradas;
    private List<Veiculo> veiculos;
    private GoogleMap map;
    private Handler UI_HANDLER = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_veiculos);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        carregarDados();

        UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, 30000);
    }

    private void carregarDados() {
        Log.i(TAG, "carregarDados Called");
        linha = (Linha) getIntent().getSerializableExtra("Linha");
        veiculos = new ArrayList<>();

        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        paradas = new ArrayList<>();
        try {
            Log.d(TAG, "Carregando paradas...");
            paradas = InthegraServiceSingleton.getParadas(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar paradas, motivo: " + e.getMessage());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VeiculosDetailActivity.this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;
        updateVeiculos(true);
    }

    @Override
    public void processFinish(List<Veiculo> result) {
        Log.i(TAG, "ProcessFinish Called");
        veiculos = result;
        TextView qtdVeiculosTxt = (TextView) findViewById(R.id.qtdVeiculosTxt);
        qtdVeiculosTxt.setText(String.valueOf(veiculos.size()));
    }

    Runnable UI_UPDTAE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            updateVeiculos(true);
            UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, 60000);
        }
    };

    private void updateVeiculos(boolean atualizaMarcadores) {
        Log.i(TAG, "UpdateVeiculos Called");
        InthegraVeiculosAsync asyncTask =  new InthegraVeiculosAsync(VeiculosDetailActivity.this);
        asyncTask.delegate = this;
        asyncTask.execute(linha);

        if(atualizaMarcadores) {
            atualizaMarcadores();
        }
    }

    private void atualizaMarcadores() {
        Log.i(TAG, "AtualizarMarcadores Called");
        Toast.makeText(VeiculosDetailActivity.this, "Atualizando marcadores...", Toast.LENGTH_SHORT).show();
        List<MarkerOptions> markers = new ArrayList<>();
        for (Parada p : paradas) {
            MarkerOptions m = new MarkerOptions()
                    .position(new LatLng(p.getLat(), p.getLong()))
                    .title(p.getCodigoParada());
            map.addMarker(m);
            markers.add(m);
        }

        for (Veiculo v : veiculos) {
            MarkerOptions m = new MarkerOptions()
                    .position(new LatLng(v.getLat(), v.getLong()))
                    .title(v.getHora())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            map.addMarker(m);
            markers.add(m);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 800, 800, 1);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "OnDestroy Called");
        UI_HANDLER.removeCallbacks(UI_UPDTAE_RUNNABLE);
        super.onDestroy();
    }
}
