package com.hcordeiro.android.InthegraApp.Activities.Linhas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.rotas.ComparadorPorProximidade;
import com.equalsp.stransthe.rotas.PontoDeInteresse;
import com.google.gson.Gson;
import com.hcordeiro.android.InthegraApp.Activities.Paradas.ParadasAdapter;
import com.hcordeiro.android.InthegraApp.Activities.Paradas.ParadasDetailActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activity de detalhe de linha, exibe as informações básicas e a lista de paradas de uma linha.
 *
 * Created by hugo on 17/05/16.
 */
public class LinhasDetailActivity extends AppCompatActivity  {
    private final String TAG = "DetailLinha";
    private Location localUsuario;
    private List<Parada> paradas;

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            localUsuario = location;
            Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
            carregarParadas(linha);
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
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linhas_detail_activity);

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Util.requestLocation(this, mLocationManager, mLocationListener);

        preencherDados();
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        if (denominacaoLinhaTxt != null) {
            denominacaoLinhaTxt.setText(linha.getDenomicao());
        }

        TextView codigoLinhaTxt = (TextView) findViewById(R.id.codigoLinhaTxt);
        if (codigoLinhaTxt != null) {
            codigoLinhaTxt.setText(linha.getCodigoLinha());
        }

        TextView origemLinhaTxt = (TextView) findViewById(R.id.origemLinhaTxt);
        if (origemLinhaTxt != null) {
            origemLinhaTxt.setText(linha.getOrigem());
        }

        TextView retornoLinhaTxt = (TextView) findViewById(R.id.retornoLinhaTxt);
        if (retornoLinhaTxt != null) {
            retornoLinhaTxt.setText(linha.getRetorno());
        }

        TextView isCircularTxt = (TextView) findViewById(R.id.isCircularTxt);
        if (isCircularTxt != null) {
            if (linha.isCircular()){
                isCircularTxt.setText(R.string.sim);
            } else {
                isCircularTxt.setText(R.string.nao);
            }
        }

        carregarParadas(linha);
    }

    private void carregarParadas(Linha linha) {
        Log.i(TAG, "carregarParadas Called");
        paradas = new ArrayList<>();
        try {
            Log.d(TAG, "Recuperando paradas...");
            paradas = InthegraServiceSingleton.getParadas(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar paradas, motivo: " + e.getMessage());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LinhasDetailActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar a lista de Paradas da Linha informada");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(LinhasDetailActivity.this, LinhasMenuActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }

        if (localUsuario != null) {
            PontoDeInteresse pontoDeInteresse
                    = new PontoDeInteresse(localUsuario.getLatitude(), localUsuario.getLongitude());
            Collections.sort(paradas, new ComparadorPorProximidade(pontoDeInteresse));
        }

        ParadasAdapter adapter = new ParadasAdapter(this, paradas);

        final ListView listView = (ListView) findViewById(R.id.paradasListView);
        if (listView != null) {
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(LinhasDetailActivity.this, ParadasDetailActivity.class);
                    Parada parada = (Parada) (listView.getItemAtPosition(position));
                    myIntent.putExtra("Parada", parada);
                    startActivity(myIntent);
                }
            });
        }
    }

    public void displayLinhasMapaActivity(View view) {
        Log.i(TAG, "displayLinhasMapaActivity Called");
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
        Intent intent = new Intent(this, LinhasMapaActivity.class);
        intent.putExtra("Linha", linha);
        String paradaJson = new Gson().toJson(paradas);
        intent.putExtra("Paradas", paradaJson);
        startActivity(intent);
    }
}
