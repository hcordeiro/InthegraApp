package com.hcordeiro.android.InthegraApp.Activities.Rotas;


import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

/**
 * Activity do menu "Que ônibus eu pego", permite a escolha de uma origem (podendo ser a localização
 * atual do usuário) e um destino para elaborar as possíveis rotas entre as duas localizações
 *
 * Created by hugo on 17/05/16.
 */
public class RotasMenuActivity extends AppCompatActivity {
    private final String TAG = "MenuRotas";
    private LatLng origem;
    private LatLng destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotas_menu_activity);
        /* Solicita a localização atual do usuário para usar como origem */
        Util.requestLocation(this, mLocationListener);

        /* Preenche a tela */
        preencherDados();
    }

    /**
     * Preenche a tela
     */
    private void preencherDados() {
        Log.d(TAG, "preencherDados Called");
        /* Recupera o botão de confirmação e o seta como desabilitado */
        Button gerarRotaBtn = (Button) findViewById(R.id.gerarRotaBtn);
        assert gerarRotaBtn != null;
        gerarRotaBtn.setEnabled(false);

        /* Recupera o switch de indicação do uso do local do usuário e o seta como desligado */
        Switch meuLocalSwtich = (Switch) findViewById(R.id.meuLocalSwtich);
        assert meuLocalSwtich != null;
        meuLocalSwtich.setChecked(false);

        /* Se o usuário permitiu os serviços de localização,
         * seta o listener que obersvará as modificações
         * no switch de indicação do uso do local do usuário.
         * Caso contrário o switch é desativado */
        if (Util.IS_LOCATION_AUTHORIZED) {
            meuLocalSwtich.setOnCheckedChangeListener(mOnCheckedChangeListener);
        } else {
            meuLocalSwtich.setEnabled(false);
        }
    }

    /**
     * Inicia a Activity que exibe o mapa para seleção da origem
     */
    public void selecionarOrigemActivity(View view) {
        Log.d(TAG, "selecionarOrigemActivity Called");
        /* Seta uma origem para, se houver, ser exibida no mapa */
        Bundle bundle = new Bundle();
        bundle.putParcelable("Origem", origem);

        /* Inicia a atividade de seleção de origem */
        Intent intent = new Intent(new Intent(this, RotasSelecionarOrigemActivity.class));
        intent.putExtra("Bundle", bundle);
        startActivityForResult(intent, 1);
    }

    /**
     * Inicia a Activity que exibe o mapa para seleção do destino
     */
    public void selecionarDestinoActivity(View view) {
        Log.d(TAG, "selecionarDestinoActivity Called");
        /* Seta uma destino para, se houver, ser exibido no mapa */
        Bundle bundle = new Bundle();
        bundle.putParcelable("Destino", destino);

        /* Inicia a atividade de seleção de destino */
        Intent intent = new Intent(new Intent(this, RotasSelecionarDestinoActivity.class));
        intent.putExtra("Bundle", bundle);
        startActivityForResult(intent, 2);
    }

    /**
     * Inicia a Activity que exibe as rotas possíveis entre a origem e o destino
     */
    public void gerarRotaActivity(View view) {
        Log.d(TAG, "gerarRotaActivity Called");
        /* Seta a origem e o destino para serem recuperados na próxima Activity*/
        Bundle bundle = new Bundle();
        bundle.putParcelable("Origem", origem);
        bundle.putParcelable("Destino", destino);

         /* Inicia a atividade que exibe as rotas possíveis entre a origem e o destino */
        Intent intent = new Intent(this, RotasEscolherActivity.class);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

    /**
     * Função que coordena as respostas das atividades de selecionar origem e selecionar destino.
     * Cada uma das atividades possui um request code para identificá-las que é usado para saber
     * deonde veio a resposta e preencher as informações corretas;
     *
     * @param requestCode utilizado para identificar qual a origem da resposta
     * @param resultCode não utilizado
     * @param data resposta
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult Called");
        /* Seleção de Origem */
        if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getParcelableExtra("Bundle");
                if (bundle != null) {
                    origem = bundle.getParcelable("Origem");

                    /* Se o usuário selecionar uma origem pelo mapa,
                     * o switch de indicação do uso do local do usuário
                     * é desligado */
                    Switch meuLocalSwtich = (Switch) findViewById(R.id.meuLocalSwtich);
                    assert meuLocalSwtich != null;
                    meuLocalSwtich.setChecked(false);
                }
            }
        }

        /* Seleção de Destino */
        if (requestCode == 2) {
            if (data != null) {
                Bundle bundle = data.getParcelableExtra("Bundle");
                if (bundle != null) {
                    destino = bundle.getParcelable("Destino");
                }
            }
        }

        /* Sempre que uma resposta é recebida,
         * a activity verifica se já possui
         * uma origem e um destino para habilitar
         * o botão de avançar */
        verificaDados();
    }

    /**
     * Verifica se a origem e o destino já estão
     * preenchidos para habilitar o botão de avançar
     */
    private void verificaDados() {
        if (origem != null && destino != null) {
            Button gerarRotaBtn = (Button) findViewById(R.id.gerarRotaBtn);
            assert gerarRotaBtn != null;
            gerarRotaBtn.setEnabled(true);
        }
    }

    /**
     * Listener responsável por obersvar a interação com
     * o switch de indicação do uso do local do usuário.
     */
    private final OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

        /**
         * Função que observa se o switch foi ligado ou desligado
         * @param buttonView ?
         * @param isChecked indica a posição do switch
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Button selectionarOrigemBtn = (Button) findViewById(R.id.selectionarOrigemBtn);
            assert selectionarOrigemBtn != null;
            /* Se o botão estiver desligado, o botão de selecionar origem é habilitado
             * Caso contrário, o botão deselecionar origem é desabilitaado.
             */
            if (!isChecked) {
                selectionarOrigemBtn.setEnabled(true);
            } else {
                selectionarOrigemBtn.setEnabled(false);
            }
            /* Sempre que uma alteração é realizada,
             * a activity verifica se já possui
             * uma origem e um destino para habilitar
             * o botão de avançar */
            verificaDados();
        }
    };

    /* Listener para a atualização da localização do usuário */
    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            Log.v(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());

            /* Recupera o switch de indicação do uso do local do usuário */
            Switch meuLocalSwtich = (Switch) findViewById(R.id.meuLocalSwtich);
            assert meuLocalSwtich != null;
            /* Se o switch de indicação do uso do local do usuário estiver ligado:
             * O botão de selecionar origem é recuperado e desabilitado, a origem
             * é setada com o local do usuário e o CheckedTextView de origem é
             * marcado como verdadeiro (essa última parte não funciona, e eu
             * desconheço a razão) */
            if (meuLocalSwtich.isChecked()) {
                Button selectionarOrigemBtn = (Button) findViewById(R.id.selectionarOrigemBtn);
                assert selectionarOrigemBtn != null;
                selectionarOrigemBtn.setEnabled(false);

                origem = new LatLng(location.getLatitude(), location.getLongitude());

                CheckedTextView origemCheckedTextView = (CheckedTextView) findViewById(R.id.origemCheckedTextView);
                assert origemCheckedTextView != null;
                origemCheckedTextView.setChecked(true);
            }

            /* Sempre que a localização é atualizada,
             * a activity verifica se já possui
             * uma origem e um destino para habilitar
             * o botão de avançar */
            verificaDados();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }
    };

}