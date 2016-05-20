package com.hcordeiro.android.InthegraApp.InthegraAPI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.equalsp.stransthe.CachedInthegraService;
import com.hcordeiro.android.InthegraApp.Util.Util;

import java.io.IOException;

/**
 * Created by hugo on 17/05/16.
 */
public class InthegraCacheAsync extends AsyncTask<Void, Void, Void> implements DialogInterface.OnCancelListener {
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog404;
    private Context mContext;
    private boolean returned404;

    public InthegraCacheAsync(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        this.progressDialog.setMessage("Carregando cache... Isso pode demorar alguns minutos");
        this.progressDialog.show();
        buildAlert404();
    }

    @Override
    protected Void doInBackground(Void... params) {
        CachedInthegraService cachedService = InthegraServiceSingleton.getInstance();

        try {
            cachedService.initialize();
        } catch (IOException e) {
            if (e.getMessage().equals(Util.ERRO_API_404)) {
                returned404 = true;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            progressDialog.dismiss();
        }
        return null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        cancel(true);
    }

    @Override
    protected void onPostExecute(Void param) {
        if (returned404) {
            alertDialog404.show();
        }
    }

    private void buildAlert404() {
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
