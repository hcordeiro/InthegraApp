package com.hcordeiro.android.InthegraApp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraCacheAsync;
import com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks.InthegraCacheAsyncResponse;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.R;

public class LoadCacheActivity extends AppCompatActivity implements InthegraCacheAsyncResponse {
    String TAG = "LoadCache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_load_cache);
        carregarCache();
    }

    private void carregarCache() {
        Log.i(TAG, "carregarCache Called");
        InthegraServiceSingleton.initInstance(this);
        InthegraCacheAsync inthegraCacheAsync = new InthegraCacheAsync(this);
        inthegraCacheAsync.delegate = this;
        inthegraCacheAsync.execute();
    }

    @Override
    public void processFinish(Boolean wasSuccessful) {
        Log.i(TAG, "processFinish Called");
        if (!wasSuccessful) {
            Button tentarNovamenteBtn = (Button) findViewById(R.id.tentarNovamenteBtn);
            tentarNovamenteBtn.setVisibility(View.VISIBLE);
            tentarNovamenteBtn.setEnabled(true);
        } else {
            Button tentarNovamenteBtn = (Button) findViewById(R.id.tentarNovamenteBtn);
            tentarNovamenteBtn.setVisibility(View.INVISIBLE);
            tentarNovamenteBtn.setEnabled(false);
            Log.i(TAG, "displayMenuRotasActivity Called");
            Intent intent = new Intent(this, MenuPrincipalActivity.class);
            startActivity(intent);
        }
    }

    public void tentarNovamente(View view) {
        Log.i(TAG, "tentarNovamente Called");
        carregarCache();
    }
}