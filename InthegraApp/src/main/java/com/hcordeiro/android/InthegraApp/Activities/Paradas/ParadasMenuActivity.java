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
import android.widget.ListView;

import com.equalsp.stransthe.Parada;
import com.hcordeiro.android.InthegraApp.Activities.MainActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParadasMenuActivity extends AppCompatActivity {
    private final String TAG = "DetailParada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu_paradas);

        preencherDados();
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
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
                            Intent intent = new Intent(ParadasMenuActivity.this, MainActivity.class);
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
                    Intent myIntent = new Intent(ParadasMenuActivity.this, ParadasDetailActivity.class);
                    Parada parada = (Parada) (listView.getItemAtPosition(position));
                    myIntent.putExtra("Parada", parada);
                    startActivity(myIntent);
                }
            });
        }
    }
}
