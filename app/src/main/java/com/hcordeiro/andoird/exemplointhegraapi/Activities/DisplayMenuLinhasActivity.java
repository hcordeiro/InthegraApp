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
import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraCachedServiceSingleton;
import com.hcordeiro.andoird.exemplointhegraapi.R;

import java.io.IOException;
import java.util.List;

public class DisplayMenuLinhasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu_linhas);
    }

    public void listarLinhas(View view) throws IOException {
        CachedInthegraService cachedService = InthegraCachedServiceSingleton.getInstance();
        final ListView listView = (ListView) findViewById(R.id.linhasListView);
        List<Linha> linhas = cachedService.getLinhas();
        ArrayAdapter<Linha> adapter = new ArrayAdapter<Linha>(this, android.R.layout.simple_list_item_1, linhas);
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
