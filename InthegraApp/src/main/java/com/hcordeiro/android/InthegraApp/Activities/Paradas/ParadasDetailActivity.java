package com.hcordeiro.android.InthegraApp.Activities.Paradas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.hcordeiro.android.InthegraApp.Activities.Linhas.LinhasDetailActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de detalhe de parada, exibe as informações básicas e a lista de linhas que passam pela
 * parada, além de direcionar o usuário para a visualização da localização da parada no mapa.
 *
 * Created by hugo on 17/05/16.
 */
public class ParadasDetailActivity extends AppCompatActivity {
    private final String TAG = "DetailParada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paradas_detail_activity);
        /* Recupera a parada selecionada no menu anterior */
        Parada parada = (Parada) getIntent().getSerializableExtra("Parada");

        /* Preenche a tela com os dados da parada e das linhas */
        preencherDados(parada);
    }

    /**
     * Preenche a tela com os dados da parada
     * @param parada que será exbida na tela
     */
    private void preencherDados(Parada parada) {
        Log.d(TAG, "preencherDados Called");

        /* Carrega as linhas que passam nessa parada */
        List<Linha> linhas = carregarLinhas(parada);

        /* Recupera e preenche o TextView da denominação da parada */
        TextView denominacaoParadaTxt = (TextView) findViewById(R.id.denominacaoParadaTxt);
        if (denominacaoParadaTxt != null) {
            denominacaoParadaTxt.setText(parada.getDenomicao());
        }

        /* Recupera e preenche o TextView do código da parada */
        TextView codigoParadaTxt = (TextView) findViewById(R.id.codigoParadaTxt);
        if (codigoParadaTxt != null) {
            codigoParadaTxt.setText(parada.getCodigoParada());
        }

        /* Recupera e preenche o TextView do endereço da parada */
        TextView enderecoParadaTxt = (TextView) findViewById(R.id.enderecoParadaTxt);
        if (enderecoParadaTxt != null) {
            enderecoParadaTxt.setText(parada.getEndereco());
        }

        /* Verifica se o usuário está online */
        if (Util.isOnline(this)) {
            /* Se o usuário estiver online, habilita o botão para
             * visualizar o parada em um mapa do GoogleMaps */
            Button verNoMapaBtn = (Button) findViewById(R.id.verNoMapaBtn);
            assert verNoMapaBtn != null;
            verNoMapaBtn.setEnabled(true);
        }

        ArrayAdapter<Linha> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, linhas);

        /* Recupera a ListView que irá conter as informações das linhas que passam naquela parada */
        final ListView listView = (ListView) findViewById(R.id.linhasListView);
        if (listView != null) {
            listView.setAdapter(adapter);

            /* Seta a função que será executada ao clicar em algum item da ListView*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /* Recupera a linha que foi selecionada na ListView */
                    Linha linha = (Linha) (listView.getItemAtPosition(position));

                    /* Inicia a atividade de detalhe de linha */
                    Intent myIntent = new Intent(ParadasDetailActivity.this, LinhasDetailActivity.class);
                    myIntent.putExtra("Linha", linha);
                    startActivity(myIntent);
                }
            });
        }
    }

    /**
     * Carrega todas as linhas de uma determinada parada à partir do cache
     * @param parada para buscar as linhas
     * @return Uma List contendo todas as linhas da parada
     */
    private List<Linha> carregarLinhas(Parada parada) {
        Log.d(TAG, "carregarLinhas Called");
        /* Diálogo de erro */
        AlertDialog alert = criarAlerta();
        List<Linha> linhas = new ArrayList<>();
        try {
            Log.v(TAG, "Carregando linhas...");
            linhas = InthegraService.getLinhas(parada);
        } catch (IOException e) {
            Log.e(TAG, this.getString(R.string.carregar_linhas) + ", motivo: " + e.getMessage());
            alert.show();
        }
        return linhas;
    }

    /**
     * Exibe o mapa com a parada escolhida
     */
    public void displayParadasMapaActivity(View view) {
        Log.i(TAG, "displayParadasMapaActivity Called");
        Parada parada = (Parada) getIntent().getSerializableExtra("Parada");
        Intent intent = new Intent(this, ParadasMapaActivity.class);
        intent.putExtra("Parada", parada);
        startActivity(intent);
    }

    /**
     * Cria o diálogo de erro que será exibido caso não
     * seja possível carregar as linhas da parada do cache
     *
     * @return diálogo de erro de carregamento de linhas
     */
    private AlertDialog criarAlerta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ParadasDetailActivity.this);
        alertBuilder.setMessage(this.getString(R.string.carregar_linhas));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton(this.getString(R.string.certo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(ParadasDetailActivity.this, ParadasMenuActivity.class);
                        startActivity(intent);
                    }
                });
        return alertBuilder.create();
    }
}