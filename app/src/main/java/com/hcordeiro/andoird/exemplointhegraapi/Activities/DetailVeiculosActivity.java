package com.hcordeiro.andoird.exemplointhegraapi.Activities;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.equalsp.stransthe.CachedInthegraService;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraCacheAsync;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraCachedServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraVeiculosAsync;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraVeiculosAsyncResponse;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailVeiculosActivity extends FragmentActivity implements OnMapReadyCallback, InthegraVeiculosAsyncResponse {

    private Linha linha;
    private List<Parada> paradas;
    private List<Veiculo> veiculos;
    private InthegraVeiculosAsync asyncTask;
    GoogleMap map;
    Handler UI_HANDLER = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_veiculos);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        linha = (Linha) getIntent().getSerializableExtra("Linha");
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        CachedInthegraService service = InthegraCachedServiceSingleton.getInstance();
        paradas = new ArrayList<>();
        try {
            paradas = service.getParadas(linha);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TextView qtdParadasTxt = (TextView) findViewById(R.id.qtdParadasTxt);
        qtdParadasTxt.setText(String.valueOf(paradas.size()));

        veiculos = new ArrayList<>();
        updateVeiculos(false);

        UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, 30000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        atualizaMarcadores();
    }

    @Override
    public void processFinish(List<Veiculo> result) {
        veiculos = result;
        TextView qtdVeiculosTxt = (TextView) findViewById(R.id.qtdVeiculosTxt);
        qtdVeiculosTxt.setText(String.valueOf(veiculos.size()));
    }

    Runnable UI_UPDTAE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            updateVeiculos(true);
            UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, 30000);
        }
    };

    private void updateVeiculos(boolean atualizaMarcadores) {
        asyncTask =  new InthegraVeiculosAsync(DetailVeiculosActivity.this);
        asyncTask.delegate = this;
        asyncTask.execute(linha);

        if(atualizaMarcadores) {
            atualizaMarcadores();
        }
    }

    private void atualizaMarcadores() {
        Toast.makeText(DetailVeiculosActivity.this, "Atualizando marcadores...", Toast.LENGTH_SHORT).show();
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
        UI_HANDLER.removeCallbacks(UI_UPDTAE_RUNNABLE);
        super.onDestroy();
    }
}
