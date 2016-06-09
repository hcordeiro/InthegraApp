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
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity do menu "Achar uma linha", exibe uma lista com todas as linhas contidas no cache
 *
 * Created by hugo on 17/05/16.
 */
public class LinhasMenuActivity extends AppCompatActivity {
    private final String TAG = "DetailParada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linhas_menu_activity);

        /* Carrega todas as linhas à partir do cache */
        List<Linha> linhas = carregarLinhas();
        /* Preenche a tela com os dados das linhas */
        preencherDados(linhas);
    }

    /**
     * Carrega todas as linhas à partir do cache
     * @return Uma List contendo todas as linhas fornecidas pelo STRANS
     */
    private List<Linha> carregarLinhas() {
        Log.d(TAG, "carregarLinhas Called");
        /* Diálogo de erro */
        AlertDialog alert = criarAlerta();
        List<Linha> linhas = new ArrayList<>();
        try {
            Log.v(TAG, "Carregando linhas...");
            linhas = InthegraService.getLinhas();
        } catch (IOException e) {
            Log.e(TAG, this.getString(R.string.carregar_linhas) + ", motivo: " + e.getMessage());
            alert.show();
        }
        return linhas;
    }

    /**
     * Preenche a tela com os dados de linhas
     * @param linhas que serão exibidas na tela
     */
    private void preencherDados(List<Linha> linhas) {
        Log.d(TAG, "preencherDados Called");
        /* Adapter necessário para função de busca */
        final LinhasAdapter adapter = new LinhasAdapter(this, linhas);
        /* Recupera a ListView que irá conter as informações das linhas */
        final ListView listView = (ListView) findViewById(R.id.linhasListView);
        /* Preenche a ListView */
        if (listView != null) {
            /* Seta o adapter que será responsável por manipular os dados da ListView */
            listView.setAdapter(adapter);

            /* Seta a função que será executada ao clicar em algum item da ListView */
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /* Recupera a linha que foi selecionada na ListView */
                    Linha linha = (Linha) (listView.getItemAtPosition(position));

                    /* Inicia a atividade de detalhe de linha */
                    Intent myIntent = new Intent(LinhasMenuActivity.this, LinhasDetailActivity.class);
                    myIntent.putExtra("Linha", linha);
                    startActivity(myIntent);
                }
            });
        }
        /* Recupera a SearchView que irá buscar na lista de linhas */
        SearchView linhaSearchView = (SearchView) findViewById(R.id.linhaSearchView);
        if (linhaSearchView != null) {
            /* Seta o listener que irá observar a interação com a caixa de busca */
            linhaSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                /* Função que será executada quando o texto da caixa de busca for submetido */
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                /* Função que será executada quando o texto da caixa de busca for modificado */
                @Override
                public boolean onQueryTextChange(String query) {
                    /* Execução da busca pelo adapter/filtro */
                    adapter.getLinhasFilter().filter(query);
                    return false;
                }
            });
        }
    }

    /**
     * Cria o diálogo de erro que será exibido caso não seja possível carregar as linhas do cache
     * @return diálogo de erro de carregamento de linhas
     */
    private AlertDialog criarAlerta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(this.getString(R.string.carregar_linhas));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton(this.getString(R.string.certo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(LinhasMenuActivity.this, MenuPrincipalActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        return alertBuilder.create();
    }
}

