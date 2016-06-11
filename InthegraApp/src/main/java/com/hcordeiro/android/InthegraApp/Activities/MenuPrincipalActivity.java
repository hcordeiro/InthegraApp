package com.hcordeiro.android.InthegraApp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hcordeiro.android.InthegraApp.Activities.Linhas.LinhasMenuActivity;
import com.hcordeiro.android.InthegraApp.Activities.Paradas.ParadasMenuActivity;
import com.hcordeiro.android.InthegraApp.Activities.Rotas.RotasMenuActivity;
import com.hcordeiro.android.InthegraApp.Activities.Veiculos.VeiculosMenuActivity;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

public class MenuPrincipalActivity extends AppCompatActivity {
    String TAG = "MenuPrincipal";
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal_activity);

        /* Ferramenta Analytics do Firebase para coletar
         * informações sobre a utilização do aplicativo
         */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /* Caso o usuário não esteja online, não é possível verificar a localização dos veículos,
         * nem calcular rotas, pois suas funcionalidades dependetem muito da conexão com a API do
         * STRANS ou do GoogleMaps.
         */
        boolean isOnline = Util.isOnline(this);
        if(isOnline) {
            /* Cadê meu ônibus */
            Button menuVeiculosBtn = (Button) findViewById(R.id.menuVeiculosBtn);
            assert menuVeiculosBtn != null;
            menuVeiculosBtn.setEnabled(true);
            /* Que ônibus eu pego? */
            Button menuRotasBtn = (Button) findViewById(R.id.menuRotasBtn);
            assert menuRotasBtn != null;
            menuRotasBtn.setEnabled(true);
        }


        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Log.i(TAG, "Token: "+ FirebaseInstanceId.getInstance().getToken());
    }

    /**
     * Exibe o menu "Achar uma linha"
     */
    public void displayMenuLinhasActivity(View view) {
        Log.d(TAG, "displayMenuLinhasActivity Called");
        /* Envia a informação para o Firebase Analytics */
        infromarFirebase("linhas");

        /* Inicia a atividade "Achar uma linha" */
        Intent intent = new Intent(this, LinhasMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Exibe o menu "Paradas próximas"
     */
    public void displayMenuParadasActivity(View view) {
        Log.d(TAG, "displayMenuParadasActivity Called");
        /* Envia a informação para o Firebase Analytics */
        infromarFirebase("paradas");

        /* Inicia a atividade "Paradas próximas" */
        Intent intent = new Intent(this, ParadasMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Exibe o menu "Cadê meu ônibus"
     */
    public void displayMenuVeiculosActivity(View view) {
        Log.d(TAG, "displayMenuVeiculosActivity Called");
        /* Envia a informação para o Firebase Analytics */
        infromarFirebase("veiculos");

        /* Inicia a atividade "Cadê meu ônibus" */
        Intent intent = new Intent(this, VeiculosMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Exibe o menu "Quê ônibus eu pego?"
     */
    public void displayMenuRotasActivity(View view) {
        Log.d(TAG, "displayMenuRotasActivity Called");
        /* Envia a informação para o Firebase Analytics */
        infromarFirebase("rotas");

        /* Inicia a atividade "Quê ônibus eu pego?" */
        Intent intent = new Intent(this, RotasMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Informa para o Firebase Analytics a utilização de um dado menu
     * @param menu
     */
    private void infromarFirebase(String menu) {
        Log.d(TAG, "infromarFirebase Called");
        Bundle bundle = new Bundle();
        bundle.putString("menu", menu);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
