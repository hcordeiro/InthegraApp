package com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.equalsp.stransthe.rotas.Rota;
import com.google.android.gms.maps.model.LatLng;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * AsyncTask responsável por carregar as possíveis rotas dados uma origem, um destino, e uma
 * distância máxima a ser percorrida a pé.
 *
 * Created by hugo on 18/05/16.
 */
public class InthegraRotasAsync extends AsyncTask<Object, Void, Set<Rota>> implements DialogInterface.OnCancelListener {
    private final String TAG = "RotasAsync";
    public InthegraRotasAsyncResponse delegate = null;
    private ProgressDialog dialog;
    private Context mContext;

    public InthegraRotasAsync(Context context){
        Log.d(TAG, "Constructor Called");
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute Called");
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        this.dialog.setMessage("Carregando rotas...");
        this.dialog.show();
    }

    @Override
    protected Set<Rota> doInBackground(Object... params) {
        Log.d(TAG, "doInBackground Called");
        LatLng p1 = (LatLng) params[0];
        LatLng p2 = (LatLng) params[1];
        Double d = (Double) params[2];

        Set<Rota> rotas = new TreeSet<>();
        try {
            rotas = InthegraService.getRotas(p1, p2, d);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            dialog.dismiss();
        }
        return rotas;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel Called");
        cancel(true);
    }

    @Override
    protected void onPostExecute(Set<Rota> rotas) {
        Log.d(TAG, "onPostExecute Called");
        delegate.processFinish(rotas);
    }
}
