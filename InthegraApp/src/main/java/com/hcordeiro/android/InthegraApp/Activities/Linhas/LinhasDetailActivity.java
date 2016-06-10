package com.hcordeiro.android.InthegraApp.Activities.Linhas;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.google.gson.Gson;
import com.hcordeiro.android.InthegraApp.Activities.Paradas.ParadasAdapter;
import com.hcordeiro.android.InthegraApp.Activities.Paradas.ParadasDetailActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de detalhe de linha, exibe as informações básicas e a lista de paradas de uma linha.
 *
 * Created by hugo on 17/05/16.
 */
public class LinhasDetailActivity extends AppCompatActivity  {
    private final String TAG = "DetailLinha";
    private ParadasAdapter adapter;
    private List<Parada> paradas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linhas_detail_activity);
        /* Solicita a localização atual do usuário para ordenar a lista
         * de paradas pela próximidade com o local do usuário */
        Util.requestLocation(this, mLocationListener);

        /* Recupera a linha selecionada no menu anterior */
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");

        /* Carrega as paradas da linha */
        paradas = carregarParadas(linha);

        /* Adapter necessário para função de ordenação das paradas */
        adapter = new ParadasAdapter(this, paradas);

        /* Preenche a tela com os dados da linha e das paradas */
        preencherDados(linha);
    }

    /**
     * Carrega todas as paradas de uma determinada linha à partir do cache
     * @param linha para buscar as paradas
     * @return Uma List contendo todas as paradas da linha
     */
    private List<Parada> carregarParadas(Linha linha) {
        Log.d(TAG, "carregarParadas Called");
        /* Diálogo de erro */
        AlertDialog alert = criarAlerta();
        List<Parada> paradas = new ArrayList<>();
        try {
            Log.v(TAG, "Carregando paradas...");
            paradas = InthegraService.getParadas(linha);
        } catch (IOException e) {
            Log.e(TAG, this.getString(R.string.erro_carregar_paradas) + ", motivo: " + e.getMessage());
            alert.show();
        }
        return paradas;
    }

    /**
     * Preenche a tela com os dados da linha
     * @param linha que será exbida na tela
     */
    private void preencherDados(Linha linha) {
        Log.d(TAG, "preencherDados Called");
        /* Recupera e preenche o TextView da denominação da linha */
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        if (denominacaoLinhaTxt != null) {
            denominacaoLinhaTxt.setText(linha.getDenomicao());
        }

        /* Recupera e preenche o TextView do código da linha */
        TextView codigoLinhaTxt = (TextView) findViewById(R.id.codigoLinhaTxt);
        if (codigoLinhaTxt != null) {
            codigoLinhaTxt.setText(linha.getCodigoLinha());
        }

        /* Recupera e preenche o TextView da origem da linha */
        TextView origemLinhaTxt = (TextView) findViewById(R.id.origemLinhaTxt);
        if (origemLinhaTxt != null) {
            origemLinhaTxt.setText(linha.getOrigem());
        }

        /* Recupera e preenche o TextView do destino da linha */
        TextView retornoLinhaTxt = (TextView) findViewById(R.id.retornoLinhaTxt);
        if (retornoLinhaTxt != null) {
            retornoLinhaTxt.setText(linha.getRetorno());
        }

        /* Recupera e preenche o TextView que indica se a linha é circular ou não */
        TextView isCircularTxt = (TextView) findViewById(R.id.isCircularTxt);
        if (isCircularTxt != null) {
            if (linha.isCircular()){
                isCircularTxt.setText(this.getString(R.string.sim));
            } else {
                isCircularTxt.setText(this.getString(R.string.nao));
            }
        }

        /* Verifica se o usuário está online */
        if (Util.isOnline(this)) {
            /* Se o usuário estiver online, habilita o botão para
             * visualizar o percurso da linha em um mapa do GoogleMaps */
            Button button = (Button) findViewById(R.id.verNoMapaBtn);
            assert button != null;
            button.setEnabled(true);
        }

        /* Recupera a ListView que irá conter as informações das paradas da linha */
        final ListView listView = (ListView) findViewById(R.id.paradasListView);
        if (listView != null) {
            /* Seta o adapter que será responsável por manipular os dados da ListView */
            listView.setAdapter(adapter);

            /* Seta a função que será executada ao clicar em algum item da ListView*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /* Recupera a parada que foi selecionada na ListView */
                    Parada parada = (Parada) (listView.getItemAtPosition(position));

                    /* Inicia a atividade de detalhe de parada */
                    Intent myIntent = new Intent(LinhasDetailActivity.this, ParadasDetailActivity.class);
                    myIntent.putExtra("Parada", parada);
                    startActivity(myIntent);
                }
            });
        }
    }

    /**
     * Exibe o mapa com o percurso da linha e suas paradas
     */
    public void displayLinhasMapaActivity(View view) {
        Log.d(TAG, "displayLinhasMapaActivity Called");
        if (Util.isOnline(this)) {
            /* Recupera a linha selecionada no menu anterior */
            Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
            /* Cria um json com as informações das paradas da linha */
            String paradaJson = new Gson().toJson(paradas);

            /* Inicia a atividade de mapa da linha */
            Intent intent = new Intent(this, LinhasMapaActivity.class);
            intent.putExtra("Linha", linha);
            intent.putExtra("Paradas", paradaJson);
            startActivity(intent);
        }
    }
    /**
     * Cria o diálogo de erro que será exibido caso não
     * seja possível carregar as paradas da linha do cache
     *
     * @return diálogo de erro de carregamento de paradas
     */
    private AlertDialog criarAlerta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LinhasDetailActivity.this);
        alertBuilder.setMessage(this.getString(R.string.erro_carregar_paradas));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton("Certo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(LinhasDetailActivity.this, LinhasMenuActivity.class);
                        startActivity(intent);
                    }
                });
        return alertBuilder.create();
    }

    /* Listener para a atualização da localização do usuário */
    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            Log.v(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            /* Ordena as paradas pela proximidade com a localização do usuário */
            adapter.sort(location);
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

}
