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

    /* Handler para coordenar a execução de uma tarefa */
    private Handler UI_HANDLER = new Handler();
    /* Tarefa para atualizar a posição dos veículos na tela */
    private Runnable UI_UPDTAE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            carregarVeiculos();
            UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, Util.VEICULOS_REFRESH_TIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.veiculos_detail_activity);

        /* Se o usuário não possuir conexão com a internet, a activity é finalizada */
        if (!Util.isOnline(this)) {
            finish();
        }

        /* Recupera o Fragment da tela que possui um mapa */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        /* Recupera de maneira assíncrona o mapa */
        mapFragment.getMapAsync(this);

        /* Inicializa uma lista de marcadores para os veículos */
        veiculosMarkers = new ArrayList<>();

        /* Carrega as paradas */
        carregarParadas();
        /* Carrega os veículos */
        carregarVeiculos();

        UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, Util.VEICULOS_REFRESH_TIME);
    }

    private void carregarParadas() {
        Log.d(TAG, "carregarParadas Called");
        /* Recupera a linha selecionada no menu anterior */
        linha = (Linha) getIntent().getSerializableExtra("Linha");
        /* Inicializa a lista de veículos */
        veiculos = new ArrayList<>();
        /* Cria um alerta para exibir caso não seja possível carregas as paradas da linha */
        AlertDialog alert = criarAlerta();

        /* Recupera e seta o TextView de denominação da linha */
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        /* Recupera as paradas da linha*/
        paradas = new ArrayList<>();
        try {
            Log.v(TAG, "Carregando paradas...");
            paradas = InthegraService.getParadas(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar paradas, motivo: " + e.getMessage());
            alert.show();
        }
        /* Recupera e seta o TextView de quantidade de paradas */
        TextView qtdParadasTxt = (TextView) findViewById(R.id.qtdParadasTxt);
        qtdParadasTxt.setText(String.valueOf(paradas.size()));
    }

    /**
     * Carrega os veículos de maneira assíncrona
     */
    private void carregarVeiculos() {
        InthegraVeiculosAsync asyncTask =  new InthegraVeiculosAsync(VeiculosMapaActivity.this);
        asyncTask.delegate = this;
        asyncTask.execute(linha);
    }

    /**
     * Função chamada quando o carregamento do mapa é finalizado
     * @param googleMap mapa carregado
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady Called");
        map = googleMap;
        paradasMarkers = new ArrayList<>();

        /* Cria e seta um CluterManager responsável por agrupar os marcadores
         * de paradas exibidos no mapa de acordo com o zoom da tela,
         * e um ParadaClusterRenderer responsável por exibir os agrupamentos
         * realizados no mapa. */
        ClusterManager<ItemParadaClusterizavel> clusterManager = new ClusterManager<>(this, map);
        clusterManager.setRenderer(new ParadaClusterRenderer(this, map, clusterManager));
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        /* Builder dos limites da tela para que o mapa exiba o percurso completo na tela */
        Builder builder = new Builder();
        /* Adiciona os marcadores de paradas */
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

    /**
     * Atualiza o mapa com a posição atual dos veículos
     */
    private void updateMapa() {
        Log.d(TAG, "updateMapa Called");
        /* Exibe a mensagem na tela */
        Toast.makeText(VeiculosMapaActivity.this, this.getString(R.string.atualizando_mapa), Toast.LENGTH_SHORT).show();
        /* Remove os marcadores de veículos existentes */
        for (Marker m : veiculosMarkers) {
            m.remove();
        }
        /* Limpa a lista de marcadores de veículos*/
        veiculosMarkers.clear();

        /* Cria novos marcadores de veículos */
        for (Veiculo v : veiculos) {
            LatLng pos = new LatLng(v.getLat(), v.getLong());
            MarkerOptions m = new MarkerOptions()
                    .position(pos)
                    .title(v.getHora())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.onibuspointer));
            veiculosMarkers.add(map.addMarker(m));
        }
    }

    /**
     * Função que processa o resultado da chamada assíncrona ao service de veículos.
     *
     * @param result List de veículos da linha
     */
    @Override
    public void processFinish(List<Veiculo> result) {
        Log.d(TAG, "ProcessFinish Called");
        veiculos = result;
        /* Recupera e preenche o TextView de quantidade de veículos */
        TextView qtdVeiculosTxt = (TextView) findViewById(R.id.qtdVeiculosTxt);
        qtdVeiculosTxt.setText(String.valueOf(veiculos.size()));
        /* Atualiza o mapa */
        updateMapa();
    }

    /**
     * IMPORTANTE!
     * Este método desabilita a função agendada de atualização de veículos
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, "OnDestroy Called");
        UI_HANDLER.removeCallbacks(UI_UPDTAE_RUNNABLE);
        super.onDestroy();
    }

    /**
     * Cria o diálogo de erro que será exibido caso não seja possível carregar as paradas do cache
     * @return diálogo de erro de carregamento de paradas
     */
    private AlertDialog criarAlerta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VeiculosMapaActivity.this);
        alertBuilder.setMessage(this.getString(R.string.erro_carregar_paradas));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton(this.getString(R.string.certo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return alertBuilder.create();
    }
}
