package com.hcordeiro.android.InthegraApp.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;

public class LoadCacheActivity extends AppCompatActivity {
    private final String TAG = "LoadCache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.load_cache_activity);

        Util.checarPermissoes(this);
        checarGooglePlay();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando...");
        progressDialog.show();

        try {
            InthegraServiceSingleton.initInstance(this);
        } catch (IOException e) {
            Log.e(TAG, "Erro ao carregar dados... motivo: " + e.getMessage());
            finish();
        } finally {
            progressDialog.dismiss();
        }
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Util.REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.IS_LOCATION_AUTHORIZED = true;
                } else {
                    Util.IS_LOCATION_AUTHORIZED = false;
                }
                return;
            }
        }
    }

    public void checarGooglePlay() {
        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (isAvailable != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, isAvailable, 0);
            errorDialog.show();
        }
    }
}