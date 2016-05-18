package com.hcordeiro.andoird.exemplointhegraapi.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.Linha;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.CachedInthegraServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisplayMenuVeiculosActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu_veiculos);

        CachedInthegraService service = CachedInthegraServiceSingleton.getInstance();
        List<Linha> linhas = new ArrayList<Linha>();
        try {
            linhas = service.getLinhas();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ListView listView = (ListView) findViewById(R.id.linhasListView);
        ArrayAdapter<Linha> adapter = new ArrayAdapter<Linha>(this, android.R.layout.simple_list_item_1, linhas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(DisplayMenuVeiculosActivity.this, DetailVeiculosActivity.class);
                Linha linha = (Linha) (listView.getItemAtPosition(position));
                myIntent.putExtra("Linha", linha);
                startActivity(myIntent);
            }
        });
    }
}
