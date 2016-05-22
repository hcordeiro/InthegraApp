package com.hcordeiro.android.InthegraApp.Activities.Linhas;

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

import com.equalsp.stransthe.Linha;
import com.hcordeiro.android.InthegraApp.Activities.MenuPrincipalActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinhasMenuActivity extends AppCompatActivity {
    private final String TAG = "DetailParada";
    private LinhasAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linhas_menu_activity);

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
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LinhasMenuActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar recuperar a lista de Linhas");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(LinhasMenuActivity.this, MenuPrincipalActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }

        adapter = new LinhasAdapter(this, linhas);

        listView = (ListView) findViewById(R.id.linhasListView);
        if (listView != null) {
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent myIntent = new Intent(LinhasMenuActivity.this, LinhasDetailActivity.class);
                    Linha linha = (Linha) (listView.getItemAtPosition(position));
                    myIntent.putExtra("Linha", linha);
                    startActivity(myIntent);
                }
            });
        }
    }

    private void carregarBusca() {
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


