package com.hcordeiro.andoird.exemplointhegraapi.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.CachedInthegraServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hugo on 17/05/16.
 */
public class DetailParadaActivity extends AppCompatActivity {
    private Parada parada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_parada);
        parada = (Parada) getIntent().getSerializableExtra("Parada");

        TextView denominacaoParadaTxt = (TextView) findViewById(R.id.denominacaoParadaTxt);
        denominacaoParadaTxt.setText(parada.getDenomicao());

        TextView codigoParadaTxt = (TextView) findViewById(R.id.codigoParadaTxt);
        codigoParadaTxt.setText(parada.getCodigoParada());

        TextView enderecoParadaTxt = (TextView) findViewById(R.id.enderecoParadaTxt);
        enderecoParadaTxt.setText(parada.getEndereco());


        List<Linha> linhas = new ArrayList<Linha>();
        try {
            linhas = CachedInthegraServiceSingleton.getLinhas(parada);
        } catch (IOException e) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetailParadaActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar recuperar a lista de Linhas da Parada informada");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(DetailParadaActivity.this, DisplayMenuParadasActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }

        ArrayAdapter<Linha> adapter = new ArrayAdapter<Linha>(this, android.R.layout.simple_list_item_1, linhas);

        final ListView listView = (ListView) findViewById(R.id.linhasListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(DetailParadaActivity.this, DetailLinhaActivity.class);
                Linha linha = (Linha) (listView.getItemAtPosition(position));
                myIntent.putExtra("Linha", linha);
                startActivity(myIntent);
            }
        });

    }

    public void displayMapActivity(View view) {
        Intent intent = new Intent(this, DisplayMapaParadaActivity.class);
        intent.putExtra("Parada", parada);
        startActivity(intent);
    }
}
