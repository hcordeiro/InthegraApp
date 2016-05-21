package com.hcordeiro.android.InthegraApp.Activities.Linhas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hcordeiro.android.InthegraApp.Activities.Paradas.ParadasDetailActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraVeiculosAsyncResponse;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity de detalhe de linha, exibe as informações básicas e a lista de paradas de uma linha.
 *
 * Created by hugo on 17/05/16.
 */
public class LinhasDetailActivity extends AppCompatActivity {
    String TAG = "DetailLinha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_linha);

        preencherDados();
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
        Linha linha = (Linha) getIntent().getSerializableExtra("Linha");
        TextView denominacaoLinhaTxt = (TextView) findViewById(R.id.denominacaoLinhaTxt);
        if (denominacaoLinhaTxt != null) {
            denominacaoLinhaTxt.setText(linha.getDenomicao());
        }

        TextView codigoLinhaTxt = (TextView) findViewById(R.id.codigoLinhaTxt);
        if (codigoLinhaTxt != null) {
            codigoLinhaTxt.setText(linha.getCodigoLinha());
        }

        TextView origemLinhaTxt = (TextView) findViewById(R.id.origemLinhaTxt);
        if (origemLinhaTxt != null) {
            origemLinhaTxt.setText(linha.getOrigem());
        }

        TextView retornoLinhaTxt = (TextView) findViewById(R.id.retornoLinhaTxt);
        if (retornoLinhaTxt != null) {
            retornoLinhaTxt.setText(linha.getRetorno());
        }

        TextView isCircularTxt = (TextView) findViewById(R.id.isCircularTxt);
        if (isCircularTxt != null) {
            if (linha.isCircular()){
                isCircularTxt.setText(R.string.sim);
            } else {
                isCircularTxt.setText(R.string.nao);
            }
        }

        carregarParadas(linha);
    }

    private void carregarParadas(Linha linha) {
        Log.i(TAG, "carregarParadas Called");
        List<Parada> paradas = new ArrayList<>();
        try {
            Log.d(TAG, "Recuperando paradas...");
            paradas = InthegraServiceSingleton.getParadas(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível recuperar paradas, motivo: " + e.getMessage());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LinhasDetailActivity.this);
            alertBuilder.setMessage("Não foi possível recuperar a lista de Paradas da Linha informada");
            alertBuilder.setCancelable(false);
            alertBuilder.setNeutralButton("Certo",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Intent intent = new Intent(LinhasDetailActivity.this, LinhasMenuActivity.class);
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
                    Intent myIntent = new Intent(LinhasDetailActivity.this, ParadasDetailActivity.class);
                    Parada parada = (Parada) (listView.getItemAtPosition(position));
                    myIntent.putExtra("Parada", parada);
                    startActivity(myIntent);
                }
            });
        }
    }

}
