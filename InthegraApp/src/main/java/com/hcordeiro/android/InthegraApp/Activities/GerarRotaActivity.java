package com.hcordeiro.android.InthegraApp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.equalsp.stransthe.rotas.Rota;
import com.google.android.gms.maps.model.LatLng;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraRotasAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraRotasAsyncResponse;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GerarRotaActivity extends AppCompatActivity implements InthegraRotasAsyncResponse {
    private final String TAG = "GerarRota";
    private Set<Rota> rotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerar_rota);
        Bundle bundle = getIntent().getParcelableExtra("Bundle");
        LatLng origem = bundle.getParcelable("Origem");
        LatLng destino = bundle.getParcelable("Destino");

        rotas = new TreeSet<>();
        carregarRotas(origem, destino, Util.DISTANCIA_MAXIMA_A_PE);

    }

    private void carregarRotas(LatLng origem, LatLng destino, Double distanciaMaxima) {
        Log.i(TAG, "carregarRotas Called");
        InthegraRotasAsync asyncTask =  new InthegraRotasAsync(GerarRotaActivity.this);
        asyncTask.delegate = this;
        asyncTask.execute(origem, destino, distanciaMaxima);
    }


    @Override
    public void processFinish(Set<Rota> result) {
        Log.i(TAG, "processFinish Called");
        rotas = result;
        List<Rota> listaRotas = new ArrayList<>();
        listaRotas.addAll(rotas);

        ArrayAdapter<Rota> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaRotas);
        final ListView listView = (ListView) findViewById(R.id.rotasListView);
        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(GerarRotaActivity.this, DetailRotaActivity.class);
                    Rota rota = (Rota) (listView.getItemAtPosition(position));
                    myIntent.putExtra("Rota", rota);
                    startActivity(myIntent);
                }
            });
        }
    }
}
