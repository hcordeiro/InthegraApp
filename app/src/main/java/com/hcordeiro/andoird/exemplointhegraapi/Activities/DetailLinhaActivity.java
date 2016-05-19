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

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.CachedInthegraServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo on 17/05/16.
 */
public class DetailLinhaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_linha);
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");

        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        denominacaoLinhaTxt.setText(linha.getDenomicao());

        TextView codigoLinhaTxt = (TextView) findViewById(R.id.codigoLinhaTxt);
        codigoLinhaTxt.setText(linha.getCodigoLinha());

        TextView origemLinhaTxt = (TextView) findViewById(R.id.origemLinhaTxt);
        origemLinhaTxt.setText(linha.getOrigem());

        TextView retornoLinhaTxt = (TextView) findViewById(R.id.retornoLinhaTxt);
        retornoLinhaTxt.setText(linha.getRetorno());

        TextView isCircularTxt = (TextView) findViewById(R.id.isCircularTxt);
        if (linha.isCircular()){
            isCircularTxt.setText("Sim");
        } else {
            isCircularTxt.setText("Não");
        }

        List<Parada> paradas = new ArrayList<Parada>();
        try {
            paradas = CachedInthegraServiceSingleton.getParadas(linha);
        } catch (IOException e) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DetailLinhaActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar recuperar a lista de Paradas da Linha informada");
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

        ArrayAdapter<Parada> adapter = new ArrayAdapter<Parada>(this, android.R.layout.simple_list_item_1, paradas);

        final ListView listView = (ListView) findViewById(R.id.paradasListView);
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
