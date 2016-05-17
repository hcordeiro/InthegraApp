package com.hcordeiro.andoird.exemplointhegraapi.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.Parada;
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraCachedServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.List;

public class DisplayMenuParadasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu_paradas);
    }

    public void listarParadas(View view) throws IOException {
        CachedInthegraService cachedService = InthegraCachedServiceSingleton.getInstance();
        final ListView listView = (ListView) findViewById(R.id.paradasListView);
        List<Parada> paradas = cachedService.getParadas();
        ArrayAdapter<Parada> adapter = new ArrayAdapter<Parada>(this, android.R.layout.simple_list_item_1, paradas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(DisplayMenuParadasActivity.this, DetailParadaActivity.class);
                Parada parada = (Parada) (listView.getItemAtPosition(position));
                myIntent.putExtra("Parada", parada);
                startActivity(myIntent);
            }
        });
    }
}
