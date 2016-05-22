package com.hcordeiro.android.InthegraApp.Activities.Paradas;

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
import android.widget.ListView;
import android.widget.SearchView;

import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.rotas.ComparadorPorProximidade;
import com.equalsp.stransthe.rotas.PontoDeInteresse;
import com.hcordeiro.android.InthegraApp.Activities.MenuPrincipalActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParadasMenuActivity extends AppCompatActivity {
    private final String TAG = "ParadasMenu";
    private Location localUsuario;
    private ParadasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paradas_menu_activity);

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Util.requestLocation(this,mLocationManager, mLocationListener);

        carregarParadas();
        carregarBusca();
    }

    private void carregarParadas() {
        Log.i(TAG, "carregarParadas Called");
        List<Parada> paradas = new ArrayList<>();
        try {
            Log.d(TAG, "Carregando paradas...");
            paradas = InthegraServiceSingleton.getParadas();
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar paradas, motivo: " + e.getMessage());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ParadasMenuActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar recuperar a lista de Paradas");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(ParadasMenuActivity.this, MenuPrincipalActivity.class);
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

        adapter = new ParadasAdapter(this, paradas);
        final ListView listView = (ListView) findViewById(R.id.paradasListView);
        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(ParadasMenuActivity.this, ParadasDetailActivity.class);
                    Parada parada = (Parada) (listView.getItemAtPosition(position));
                    myIntent.putExtra("Parada", parada);
                    startActivity(myIntent);
                }
            });
        }
    }

    private void carregarBusca() {
        SearchView paradaSearchView = (SearchView) findViewById(R.id.paradaSearchView);

        if (paradaSearchView != null) {
            paradaSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    adapter.getParadasFilter().filter(query);
                    return false;
                }
            });
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            localUsuario = location;
            carregarParadas();
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

}
