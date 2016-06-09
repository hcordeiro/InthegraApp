package com.hcordeiro.android.InthegraApp.Activities.Paradas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.equalsp.stransthe.Parada;
import com.hcordeiro.android.InthegraApp.Activities.MenuPrincipalActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Activity do menu "Paradas próximas", exibe uma lista com todas as paradas contidas no cache.
 * Caso o usuário tenha habilitado os serviços de localização, as paradas são ordenadas pela
 * proximidade com a localização do usuário.
 *
 * Created by hugo on 17/05/16.
 */
public class ParadasMenuActivity extends AppCompatActivity {
    private final String TAG = "ParadasMenu";
    private ParadasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paradas_menu_activity);
        /* Solicita a localização atual do usuário para ordenar a lista
         * de paradas pela próximidade com o local do usuário */
        Util.requestLocation(this, mLocationListener);

        /* Carrega todas as paradas à partir do cache */
        List<Parada> paradas = carregarParadas();

        /* Preenche a tela com os dados das paradas */
        preencherDados(paradas);
    }

    /**
     * Carrega todas as paradas à partir do cache
     * @return Uma List contendo todas as paradas fornecidas pelo STRANS
     */
    private List<Parada> carregarParadas() {
        Log.d(TAG, "carregarParadas Called");
        /* Diálogo de erro */
        AlertDialog alert = criarAlerta();
        List<Parada> paradas = new ArrayList<>();
        try {
            Log.v(TAG, "Carregando paradas...");
            paradas = InthegraService.getParadas();
        } catch (IOException e) {
            Log.e(TAG, this.getString(R.string.carregar_paradas) + ", motivo: " + e.getMessage());
            alert.show();
        }
        return paradas;
    }

    /**
     * Preenche a tela com os dados de paradas
     * @param paradas que serão exibidas na tela
     */
    private void preencherDados(List<Parada> paradas) {
        Log.d(TAG, "preencherDados Called");

        /* Adapter necessário para função de busca */
        adapter = new ParadasAdapter(this, paradas);

        /* Recupera a ListView que irá conter as informações das paradas */
        final ListView paradasListView = (ListView) findViewById(R.id.paradasListView);

        /* Preenche a ListView */
        if (paradasListView != null) {
            /* Seta o adapter que será responsável por manipular os dados da ListView */
            paradasListView.setAdapter(adapter);

            /* Seta a função que será executada ao clicar em algum item da ListView */
            paradasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /* Recupera a parada que foi selecionada na ListView */
                    Parada parada = (Parada) (paradasListView.getItemAtPosition(position));

                     /* Inicia a atividade de detalhe de parada */
                    Intent myIntent = new Intent(ParadasMenuActivity.this, ParadasDetailActivity.class);
                    myIntent.putExtra("Parada", parada);
                    startActivity(myIntent);
                }
            });
        }

        /* Recupera a SearchView que irá buscar na lista de paradas */
        SearchView paradaSearchView = (SearchView) findViewById(R.id.paradaSearchView);
        if (paradaSearchView != null) {
            /* Seta o listener que irá observar a interação com a caixa de busca */
            paradaSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                /* Função que será executada quando o texto da caixa de busca for submetido */
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                /* Função que será executada quando o texto da caixa de busca for modificado */
                @Override
                public boolean onQueryTextChange(String query) {
                    /* Execução da busca pelo adapter/filtro */
                    adapter.getParadasFilter().filter(query);
                    return false;
                }
            });
        }

    }
    /**
     * Cria o diálogo de erro que será exibido caso não seja possível carregar as paradas do cache
     * @return diálogo de erro de carregamento de paradas
     */
    private AlertDialog criarAlerta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ParadasMenuActivity.this);
        alertBuilder.setMessage(this.getString(R.string.carregar_paradas));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton(this.getString(R.string.certo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(ParadasMenuActivity.this, MenuPrincipalActivity.class);
                        startActivity(intent);
                    }
                });
        return alertBuilder.create();
    }

    /* Listener para a atualização da localização do usuário */
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            /* Ordena as paradas pela proximidade com a localização do usuário */
            adapter.sort(location);
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