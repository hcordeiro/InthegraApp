package com.hcordeiro.android.InthegraApp.Activities.Rotas;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraDirectionsAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsyncResponse;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@SuppressWarnings("MissingPermission")
public class RotaDetailActivity extends FragmentActivity implements OnMapReadyCallback, InthegraVeiculosAsyncResponse {
    private final String TAG = "DetailRota";
    private GoogleMap map;

    private Parada primeiraParada;
    private ArrayList<Linha> linhas;
    private List<Veiculo> veiculos;
    private List<Marker> veiculosMarkers;

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
        setContentView(R.layout.rotas_detail_activity);

        Util.requestLocation(this, mLocationListener);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        veiculosMarkers = new ArrayList<>();
        primeiraParada = null;

        Rota rota = (Rota) getIntent().getSerializableExtra("Rota");

        List<Trecho> trechos = rota.getTrechos();
        linhas = new ArrayList<>();
        for (Trecho t : trechos) {
            if (t.getLinha() != null) {
                if (!linhas.contains(t.getLinha())) {
                    linhas.add(t.getLinha());
                }
            }
        }

        UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, Util.VEICULOS_REFRESH_TIME);
    }

    private void carregarVeiculos() {
        for (Linha linha : linhas) {
            InthegraVeiculosAsync asyncTask =  new InthegraVeiculosAsync(RotaDetailActivity.this);
            asyncTask.delegate = this;
            asyncTask.execute(linha);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "OnMapReady Called");
        map = googleMap;
        if (Util.IS_LOCATION_AUTHORIZED) {
            map.setMyLocationEnabled(true);
        }

        adicionarMarcadores();
    }

    private void adicionarMarcadores() {
        Log.i(TAG, "adicionarMarcadores Called");
        Rota rota = (Rota) getIntent().getSerializableExtra("Rota");
        Builder builder = new Builder();
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
                if (primeiraParada == null) {
                    primeiraParada = p;
                }

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

        Localizacao inicio = rota.getTrechos().get(0).getOrigem();
        LatLng pos = new LatLng(inicio.getLat(), inicio.getLong());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 15);
        map.animateCamera(cameraUpdate);
    }

    private void getDirections(Trecho trecho) {
        Log.i(TAG, "GetDirections Called");
        InthegraDirectionsAsync asyncTask =  new InthegraDirectionsAsync(RotaDetailActivity.this, map);
        asyncTask.execute(trecho);
    }

    public void solicitarAviso(View view) {
        String token = FirebaseInstanceId.getInstance().getToken();
        String codigoLinha = linhas.get(0).getCodigoLinha();
        String codigoParada = primeiraParada.getCodigoParada();
        int quantidadeParadas = 3;

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        RemoteMessage remoteMessage = new RemoteMessage.Builder("252844749965@gcm.googleapis.com")
                .setMessageId(Integer.toString(Util.msgId.incrementAndGet()))
                .addData("action", "esperar_onibus")
                .addData("registration_token", token)
                .addData("codigo_linha", codigoLinha)
                .addData("codigo_parada", codigoParada)
                .addData("quantidades_paradas", String.valueOf(quantidadeParadas))
                .build();

        fm.send(remoteMessage);
        Log.i(TAG, "Message sent: " + remoteMessage.getMessageId());
    }

    @Override
    public void processFinish(List<Veiculo> result) {
        Log.i(TAG, "ProcessFinish Called");
        veiculos = result;
        updateMapa();
    }

    private void updateMapa() {
        Log.i(TAG, "updateMapa Called");
        Toast.makeText(RotaDetailActivity.this, "Atualizando mapa...", Toast.LENGTH_SHORT).show();
        for (Marker m : veiculosMarkers) {
            m.remove();
        }
        veiculosMarkers.clear();

        for (Veiculo v : veiculos) {
            LatLng pos = new LatLng(v.getLat(), v.getLong());
            MarkerOptions m = new MarkerOptions()
                    .position(pos)
                    .title(v.getHora())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            veiculosMarkers.add(map.addMarker(m));
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Toast.makeText(RotaDetailActivity.this, "Atualizando localização...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, Util.ZOOM);
            map.animateCamera(cameraUpdate);
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

    @Override
    public void onDestroy() {
        Log.i(TAG, "OnDestroy Called");
        UI_HANDLER.removeCallbacks(UI_UPDTAE_RUNNABLE);
        super.onDestroy();
    }
}
