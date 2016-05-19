package com.hcordeiro.andoird.exemplointhegraapi.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraVeiculosAsyncResponse;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.text.DecimalFormat;
import java.util.List;

public class DetailRotaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    Rota rota;
    private Handler UI_HANDLER = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rota);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rota = (Rota) getIntent().getSerializableExtra("Rota");

        TextView qtdTrechosTxt = (TextView) findViewById(R.id.qtdTrechosTxt);
        qtdTrechosTxt.setText(String.valueOf(rota.getTrechos().size()));

        TextView distanciaTotalTxt = (TextView) findViewById(R.id.distanciaTotalTxt);
        DecimalFormat df = new DecimalFormat("#.00");
        String distanciaTotal = String.valueOf(df.format(rota.getDistanciaTotal())) + " m";
        distanciaTotalTxt.setText(distanciaTotal);

//        TextView tempoEstimadoTxt = (TextView) findViewById(R.id.tempoEstimadoTxt);
//        tempoEstimadoTxt.setText(String.valueOf(rota.getTempoTotal()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        PolylineOptions polyLine = new PolylineOptions().color(Color.BLUE).width((float) 7.0);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<Trecho> trechos = rota.getTrechos();
        int nTrecho = 0;
        for (Trecho trecho : trechos) {
            nTrecho = nTrecho + 1;
            Localizacao origem = trecho.getOrigem();
            Localizacao destino = trecho.getDestino();

            String complementoTitulo = "";
            if (trecho.getLinha() != null) {
                complementoTitulo = " " + trecho.getLinha().getCodigoLinha();
            } else {
                complementoTitulo = " Trecho " + nTrecho;
            }

            MarkerOptions mOrigem = new MarkerOptions()
                    .position(new LatLng(origem.getLat(), origem.getLong()))
                    .title("Origem Trecho" + complementoTitulo);

            MarkerOptions mDestino = new MarkerOptions()
                    .position(new LatLng(destino.getLat(), destino.getLong()))
                    .title("Destino " + complementoTitulo);

            map.addMarker(mOrigem);
            map.addMarker(mDestino);
//            polyLine.add(new LatLng(origem.getLat(), origem.getLong()));
//            polyLine.add(new LatLng(origem.getLat(), origem.getLong()));
            builder.include(mOrigem.getPosition());
            builder.include(mDestino.getPosition());
        }

//        map.addPolyline(polyLine);

        LatLngBounds bounds = builder.build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 800, 800, 1);
        map.animateCamera(cameraUpdate);
    }
}
