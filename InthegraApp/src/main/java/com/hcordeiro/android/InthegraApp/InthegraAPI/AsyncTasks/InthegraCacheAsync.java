package com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.equalsp.stransthe.CachedInthegraService;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraServiceSingleton;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;

/**
 * AsyncTask responsável por carregar os dados da API Inthegra (1) da internet e salvá-los
 * localmente, ou (2) carregar os dados de um arquivo local;
 *
 * Created by hugo on 17/05/16.
 */
public class InthegraCacheAsync extends AsyncTask<Void, Void, Boolean> implements OnCancelListener {
    private final String TAG = "CacheAsync";

    private Context mContext;
    public InthegraCacheAsyncResponse delegate = null;

    private ProgressDialog progressDialog;
    private AlertDialog alertDialog404;

    private boolean returned404;

    public InthegraCacheAsync(Context context){
        Log.i(TAG, "Constructor Called");
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute Called");
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        this.progressDialog.setMessage("Carregando cache... Isso pode demorar alguns minutos");
        this.progressDialog.show();
        buildAlert404();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.i(TAG, "doInBackground Called");
        CachedInthegraService cachedService = InthegraServiceSingleton.getInstance();
        boolean wasSuccesfful = true;

        try {
            Log.d(TAG, "Inicializando cache...");
            cachedService.initialize();
        } catch (IOException e) {
            Log.e(TAG, "Não foi possíel iniciar o cache, motivo: " + e.getMessage());
            wasSuccesfful = false;
            if (e.getMessage().equals(Util.ERRO_API_404)) {
                returned404 = true;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            progressDialog.dismiss();
        }

        return wasSuccesfful;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.i(TAG, "onCancel Called");
        cancel(true);
    }

    @Override
    protected void onPostExecute(Boolean wasSuccessful) {
        Log.i(TAG, "onPostExecute Called");
        if (returned404) {
            alertDialog404.show();
        }
        delegate.processFinish(wasSuccessful);
    }

    private void buildAlert404() {
        Log.i(TAG, "buildAlert404 Called");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setMessage("Não foi possível conectar com a API do Strans");
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton("Certo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog404 = alertBuilder.create();
    }
}
