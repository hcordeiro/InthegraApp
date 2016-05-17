package com.hcordeiro.andoird.exemplointhegraapi.Activities;

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
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraCachedServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo on 17/05/16.
 */
public class DetailParadaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_parada);
        Parada parada = (Parada) getIntent().getSerializableExtra("Parada");

        TextView denominacaoParadaTxt = (TextView) findViewById(R.id.denominacaoParadaTxt);
        denominacaoParadaTxt.setText(parada.getDenomicao());

        TextView codigoParadaTxt = (TextView) findViewById(R.id.codigoParadaTxt);
        codigoParadaTxt.setText(parada.getCodigoParada());

        TextView enderecoParadaTxt = (TextView) findViewById(R.id.enderecoParadaTxt);
        enderecoParadaTxt.setText(parada.getEndereco());


        CachedInthegraService service = InthegraCachedServiceSingleton.getInstance();
        List<Linha> linhas = new ArrayList<Linha>();
        try {
            linhas = service.getLinhas(parada);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ListView listView = (ListView) findViewById(R.id.linhasListView);
        ArrayAdapter<Linha> adapter = new ArrayAdapter<Linha>(this, android.R.layout.simple_list_item_1, linhas);
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
}
