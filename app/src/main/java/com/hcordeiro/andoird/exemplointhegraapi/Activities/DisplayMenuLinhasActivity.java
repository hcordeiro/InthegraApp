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

import com.equalsp.stransthe.Linha;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.CachedInthegraServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayMenuLinhasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu_linhas);

        List<Linha> linhas = new ArrayList<>();
        try {
            linhas = CachedInthegraServiceSingleton.getLinhas();
        } catch (IOException e) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DisplayMenuLinhasActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar recuperar a lista de Linhas");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(DisplayMenuLinhasActivity.this, MainActivity.class);
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
                Intent myIntent = new Intent(DisplayMenuLinhasActivity.this, DetailLinhaActivity.class);
                Linha linha = (Linha) (listView.getItemAtPosition(position));
                myIntent.putExtra("Linha", linha);
                startActivity(myIntent);
            }
        });
    }
}
