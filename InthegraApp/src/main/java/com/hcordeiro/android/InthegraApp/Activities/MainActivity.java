package com.hcordeiro.android.InthegraApp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.InthegraService;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraCacheAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraCacheAsyncResponse;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

public class MainActivity extends AppCompatActivity implements InthegraCacheAsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.verifyStoragePermissions(this);
        InthegraServiceSingleton.initInstance();
        InthegraCacheAsync inthegraCacheAsync = new InthegraCacheAsync(this);
        inthegraCacheAsync.delegate = this;
        inthegraCacheAsync.execute();
    }
    public void displayMenuParadasActivity(View view) {
        Intent intent = new Intent(this, DisplayMenuParadasActivity.class);
        startActivity(intent);
    }

    public void displayMenuLinhasActivity(View view) {
        Intent intent = new Intent(this, DisplayMenuLinhasActivity.class);
        startActivity(intent);
    }

    public void displayMenuVeiculosActivity(View view) {
        Intent intent = new Intent(this, DisplayMenuVeiculosActivity.class);
        startActivity(intent);
    }

    public void displayMenuRotasActivity(View view) {
        Intent intent = new Intent(this, DisplayMenuRotasActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinish(Boolean wasSuccessful) {
        if (!wasSuccessful) {
            Button menuLinhasBtn = (Button) findViewById(R.id.menuLinhasBtn);
            menuLinhasBtn.setEnabled(false);

            Button menuParadasBtn = (Button) findViewById(R.id.menuParadasBtn);
            menuParadasBtn.setEnabled(false);

            Button menuRotasBtn = (Button) findViewById(R.id.menuRotasBtn);
            menuRotasBtn.setEnabled(false);

            Button menuVeiculosBtn = (Button) findViewById(R.id.menuVeiculosBtn);
            menuVeiculosBtn.setEnabled(false);
        }
    }
}
