package com.hcordeiro.android.InthegraApp.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de detalhe de linha, exibe as informações básicas e a lista de paradas de uma linha.
 *
 * Created by hugo on 17/05/16.
 */
public class DetailLinhaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = "DetailLinha";
        Log.i(TAG, "OnCreate Called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_linha);
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

        List<Parada> paradas = new ArrayList<>();
        try {
            Log.d(TAG, "Recuperando paradas...");
            paradas = InthegraServiceSingleton.getParadas(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar paradas, motivo: " + e.getMessage());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetailLinhaActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar a lista de Paradas da Linha informada");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(DetailLinhaActivity.this, DisplayMenuLinhasActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }

        ArrayAdapter<Parada> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, paradas);

        final ListView listView = (ListView) findViewById(R.id.paradasListView);
        if (listView != null) {
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(DetailLinhaActivity.this, DetailParadaActivity.class);
                    Parada parada = (Parada) (listView.getItemAtPosition(position));
                    myIntent.putExtra("Parada", parada);
                    startActivity(myIntent);
                }
            });
        }
    }
}
