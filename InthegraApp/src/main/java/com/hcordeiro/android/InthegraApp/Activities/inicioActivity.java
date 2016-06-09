package com.hcordeiro.android.InthegraApp.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;

public class inicioActivity extends AppCompatActivity {
    private final String TAG = "Inicio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inicio_activity);

        /* Verifica as permissões de localização */
        Util.checarPermissoes(this);

        /* Verifica se a disponibilidade do google play services */
        checarGooglePlay();

        /* Dialogo de carregamento */
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getString(R.string.carregando));
        progressDialog.show();

        /* Dialogo de erro */
        AlertDialog alertDialog = criarAlerta();

        try {
            Log.v(TAG, "Iniciando InthegraService...");
            InthegraService.initInstance(this);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível iniciar o InthegraService, motivo: " + e.getMessage());
            progressDialog.dismiss();
            alertDialog.show();
            finish();
        } finally {
            progressDialog.dismiss();
        }

        /* Inicia a atividade do menu principal */
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        startActivity(intent);

        /*
         * Finaliza a atividade atual, para que ela não fique na pilha de atividades e, se
         * o usuário apertar o botão de voltar à partir do menu principal, sair da aplicação
         */
        finish();
    }

    private AlertDialog criarAlerta() {
        Log.v(TAG, "criarAlerta Called");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(this.getString(R.string.iniciar_app));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton(this.getString(R.string.certo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return alertBuilder.create();
    }

    public void checarGooglePlay() {
        Log.d(TAG, "checarGooglePlay Called");
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (isAvailable != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, isAvailable, 0);
            errorDialog.show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult Called");
        switch (requestCode) {
            case Util.REQUEST_ACCESS_LOCATION: {
                /* If request is cancelled, the result arrays are empty. */
                Util.IS_LOCATION_AUTHORIZED = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
    }
}