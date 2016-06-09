package com.hcordeiro.android.InthegraApp.Activities.Rotas;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Localizacao;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;
import com.equalsp.stransthe.rotas.Rota;
import com.equalsp.stransthe.rotas.Trecho;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraDirectionsAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsyncResponse;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.GoogleMaps.ItemParadaClusterizavel;
import com.hcordeiro.android.InthegraApp.Util.GoogleMaps.ParadaClusterRenderer;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity de mapa de rota, exibe um mapa com a rota selecionada
 *
 * Created by hugo on 17/05/16.
 */
@SuppressWarnings("MissingPermission")
public class RotasMapaActivity extends FragmentActivity implements OnMapReadyCallback, InthegraVeiculosAsyncResponse {
    private final String TAG = "DetailRota";
    private GoogleMap map;

    private Parada primeiraParada;
    private ArrayList<Linha> linhas;
    private List<Veiculo> veiculos;
    private List<Marker> veiculosMarkers;

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
        setContentView(R.layout.rotas_detail_activity);
        /* Solicita a localização atual do usuário
         * para exibi-la no mapa */
        Util.requestLocation(this, mLocationListener);

        /* Se o usuário não possuir conexão com a internet, a activity é finalizada */
        if (!Util.isOnline(this)) {
            finish();
        }

        /* Recupera o Fragment da tela que possui um mapa */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        /* Recupera de maneira assíncrona o mapa */
        mapFragment.getMapAsync(this);

        /* Lista de marcadores de veículos */
        veiculosMarkers = new ArrayList<>();

        /* Primeira parada da rota */
        primeiraParada = null;

        /* Recupera rota selecionada no menu anterior */
        Rota rota = (Rota) getIntent().getSerializableExtra("Rota");

        /* Carrega as linhas de ônibus pela qual o trecho vai passar.
         * Atualmente o service só retorna rotas com no máximo uma linha.*/
        List<Trecho> trechos = rota.getTrechos();
        linhas = new ArrayList<>();
        for (Trecho t : trechos) {
            if (t.getLinha() != null) {
                if (!linhas.contains(t.getLinha())) {
                    linhas.add(t.getLinha());
                }
            }
        }

        /* Executa a primeira atualização da localização dos veículos */
        UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, 0);
    }

    /* Recupera os veículos de todas as linhas de maneira assíncrona */
    private void carregarVeiculos() {
        for (Linha linha : linhas) {
            InthegraVeiculosAsync asyncTask =  new InthegraVeiculosAsync(RotasMapaActivity.this);
            asyncTask.delegate = this;
            asyncTask.execute(linha);
        }
    }

    /**
     * Função chamada quando o carregamento do mapa é finalizado
     * @param googleMap mapa carregado
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady Called");
        map = googleMap;
        /* Se o usuário permitiu os serviços de localização,
         * o botão "meu local" é habilitado no mapa */
        if (Util.IS_LOCATION_AUTHORIZED) {
            map.setMyLocationEnabled(true);
        }

        /* Adiciona ao mapa os marcadores da rota (Origem, Paradas e Destino) */
        adicionarMarcadores();
    }

    /**
     * Adiciona ao mapa os marcadores da rota (Origem, Paradas e Destino)
     */
    private void adicionarMarcadores() {
        Log.d(TAG, "adicionarMarcadores Called");
        /* Recupera a rota selecionada no menu anterior */
        Rota rota = (Rota) getIntent().getSerializableExtra("Rota");

        List<Trecho> trechos = rota.getTrechos();
        int trechosAPe = 0;

        /* Cria e seta um CluterManager responsável por agrupar os marcadores
         * de paradas exibidos no mapa de acordo com o zoom da tela,
         * e um ParadaClusterRenderer responsável por exibir os agrupamentos
         * realizados no mapa. */
        ClusterManager<ItemParadaClusterizavel> clusterManager = new ClusterManager<>(this, map);
        clusterManager.setRenderer(new ParadaClusterRenderer(this, map, clusterManager));
        map.setOnCameraChangeListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        /* Marcador Padrão */
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker();
        for (Trecho trecho : trechos) {
            /* Se o trecho não possui linha, e a primeira parada é nula,
             * isso significa que é o primeiro trecho, da origem até a
             * primeira parada */
            if (trecho.getLinha() == null && primeiraParada == null) {
                trechosAPe = trechosAPe + 1;
                /* Pega a rota do google maps */
                getDirections(trecho);
                /* Seta o marcador de origem da rota */
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.startpointer);
            }

            /* Dados da origem do trecho */
            Localizacao origem = trecho.getOrigem();
            String tituloOrigem = "Origem do trecho a pé " + trechosAPe;

            /* Se a origem for uma Parada */
            if (origem instanceof Parada) {
                Parada p = (Parada) origem;
                if (primeiraParada == null) {
                    primeiraParada = p;
                }
                /* Altera o titulo da origem para a denominação da parada */
                tituloOrigem = p.getDenomicao();

                /* Seta o marcador de parada */
                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.paradapointer);

                /* Adiciona a parada ao cluster */
                clusterManager.addItem(new ItemParadaClusterizavel(p));
            }

            /* Cria o marcador da origem */
            MarkerOptions mOrigem = new MarkerOptions()
                    .position(new LatLng(origem.getLat(), origem.getLong()))
                    .title(tituloOrigem)
                    .icon(bitmapDescriptor);

            map.addMarker(mOrigem);
        }

        /* Adiciona o ultimo trecho (até o destino) ao mapa */
        Trecho ultimoTrecho = trechos.get(trechos.size()-1);
        /* Cria o marcador de destino */
        MarkerOptions destinoFinal = new MarkerOptions()
                .position(new LatLng(ultimoTrecho.getDestino().getLat(), ultimoTrecho.getDestino().getLong()))
                .title("Destino Final")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.finishpointer));
        map.addMarker(destinoFinal);

        /* Recupera a localização da origem da rota */
        Localizacao inicio = rota.getTrechos().get(0).getOrigem();
        LatLng pos = new LatLng(inicio.getLat(), inicio.getLong());

        /* Seta o centro do mapa e anima a camêra para a vizualização indicada */
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 15);
        map.animateCamera(cameraUpdate);
    }

    /**
     * Recupera a rota do google maps de maneira assíncrona
     * @param trecho que será buscado pelo google maps
     */
    private void getDirections(Trecho trecho) {
        Log.d(TAG, "GetDirections Called");
        InthegraDirectionsAsync asyncTask =  new InthegraDirectionsAsync(RotasMapaActivity.this, map);
        asyncTask.execute(trecho);
    }

    /**
     * Função que processa o resultado da chamada assíncrona ao service de veículos.
     *
     * @param result List de veículos da rota
     */
    @Override
    public void processFinish(List<Veiculo> result) {
        Log.d(TAG, "ProcessFinish Called");
        veiculos = result;
        /* Atualiza a posição dos veículos no mapa */
        updateMapa();
    }

    /**
     * Atualiza a posição dos veículos no mapa
     */
    private void updateMapa() {
        Log.d(TAG, "updateMapa Called");
        /* Exibe mensagem na tela */
        Toast.makeText(RotasMapaActivity.this, this.getString(R.string.atualizando_mapa), Toast.LENGTH_SHORT).show();
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

    /* Listener para a atualização da localização do usuário */
    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            Log.v(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            /* Centraliza o mapa na nova localização do usuário */
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, Util.ZOOM);
            map.animateCamera(cameraUpdate);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }
    };

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
}