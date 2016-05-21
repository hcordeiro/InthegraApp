package com.hcordeiro.android.InthegraApp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hcordeiro.android.InthegraApp.Activities.Linhas.LinhasMenuActivity;
import com.hcordeiro.android.InthegraApp.Activities.Paradas.ParadasMenuActivity;
import com.hcordeiro.android.InthegraApp.Activities.Rotas.RotasMenuActivity;
import com.hcordeiro.android.InthegraApp.Activities.Veiculos.VeiculosMenuActivity;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraCacheAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraCacheAsyncResponse;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;


public class MainActivity extends AppCompatActivity implements InthegraCacheAsyncResponse {
    String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carregarCache();

    }

    private void carregarCache() {
        Log.i(TAG, "carregarCache Called");
        Util.verifyStoragePermissions(this);
        InthegraServiceSingleton.initInstance();
        InthegraCacheAsync inthegraCacheAsync = new InthegraCacheAsync(this);
        inthegraCacheAsync.delegate = this;
        inthegraCacheAsync.execute();
    }

    public void displayMenuParadasActivity(View view) {
        Log.i(TAG, "displayMenuParadasActivity Called");
        Intent intent = new Intent(this, ParadasMenuActivity.class);
        startActivity(intent);
    }

    public void displayMenuLinhasActivity(View view) {
        Log.i(TAG, "displayMenuLinhasActivity Called");
        Intent intent = new Intent(this, LinhasMenuActivity.class);
        startActivity(intent);
    }

    public void displayMenuVeiculosActivity(View view) {
        Log.i(TAG, "displayMenuVeiculosActivity Called");
        Intent intent = new Intent(this, VeiculosMenuActivity.class);
        startActivity(intent);
    }

    public void displayMenuRotasActivity(View view) {
        Log.i(TAG, "displayMenuRotasActivity Called");
        Intent intent = new Intent(this, RotasMenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(Boolean wasSuccessful) {
        Log.i(TAG, "processFinish Called");
        if (!wasSuccessful) {
            Button menuLinhasBtn = (Button) findViewById(R.id.menuLinhasBtn);
            if (menuLinhasBtn != null) {
                menuLinhasBtn.setEnabled(false);
            }

            Button menuParadasBtn = (Button) findViewById(R.id.menuParadasBtn);
            if (menuParadasBtn != null) {
                menuParadasBtn.setEnabled(false);
            }

            Button menuRotasBtn = (Button) findViewById(R.id.menuRotasBtn);
            if (menuRotasBtn != null) {
                menuRotasBtn.setEnabled(false);
            }

            Button menuVeiculosBtn = (Button) findViewById(R.id.menuVeiculosBtn);
            if (menuVeiculosBtn != null) {
                menuVeiculosBtn.setEnabled(false);
            }
        }
    }
}
