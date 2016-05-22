package com.hcordeiro.android.InthegraApp.Activities.Veiculos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.Linha;
import com.hcordeiro.android.InthegraApp.Activities.Linhas.LinhasAdapter;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de menu de veículos.
 *
 * Created by hugo on 17/05/16.
 */
public class VeiculosMenuActivity extends AppCompatActivity {
    String TAG = "MenuVeiculos";
    private LinhasAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.veiculos_menu_activity);

        carregarLinhas();
        carregarBusca();


    }
    private void carregarLinhas() {
        Log.i(TAG, "carregarLinhas Called");
        List<Linha> linhas = new ArrayList<>();
        try {
            Log.d(TAG, "Carregando linhas...");
            linhas = InthegraServiceSingleton.getLinhas();
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar linhas, motivo: " + e.getMessage());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Não foi possível recuperar as linhas de ônibus");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
        }

        if (!linhas.isEmpty()) {
            final ListView listView = (ListView) findViewById(R.id.linhasListView);

            adapter = new LinhasAdapter(this, linhas);

            if (listView != null) {
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent myIntent = new Intent(VeiculosMenuActivity.this, VeiculosDetailActivity.class);
                        Linha linha = (Linha) (listView.getItemAtPosition(position));
                        myIntent.putExtra("Linha", linha);
                        startActivity(myIntent);
                    }
                });
            }
        }
    }

    private void carregarBusca() {
        Log.i(TAG, "carregarBusca Called");

        SearchView linhaSearchView = (SearchView) findViewById(R.id.linhaSearchView);
        if (linhaSearchView != null) {
            linhaSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    adapter.getLinhasFilter().filter(query);
                    return false;
                }
            });
        }
    }
}