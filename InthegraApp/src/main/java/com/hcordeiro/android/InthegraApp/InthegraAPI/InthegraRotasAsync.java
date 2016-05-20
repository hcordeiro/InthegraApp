package com.hcordeiro.android.InthegraApp.InthegraAPI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.equalsp.stransthe.rotas.Rota;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by hugo on 18/05/16.
 */
public class InthegraRotasAsync extends AsyncTask<Object, Void, Set<Rota>> implements DialogInterface.OnCancelListener {
    public InthegraRotasAsyncResponse delegate = null;
    private ProgressDialog dialog;
    private AlertDialog alert;
    private Context mContext;
    private boolean wasUnsuccessful;

    public InthegraRotasAsync(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        this.dialog.setMessage("Carregando rotas...");
        this.dialog.show();
    }

    @Override
    protected Set<Rota> doInBackground(Object... params) {
        LatLng p1 = (LatLng) params[0];
        LatLng p2 = (LatLng) params[1];
        Double d = (Double) params[2];

        Set<Rota> rotas = new TreeSet<>();
        try {
            rotas = InthegraServiceSingleton.getRotas(p1, p2, d);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            dialog.dismiss();
        }
        return rotas;
    }

    @Override
    public void onCancel(DialogInterface dialog) {cancel(true);}

    @Override
    protected void onPostExecute(Set<Rota> rotas) {
        if (wasUnsuccessful) {
            alert.show();
        }
        delegate.processFinish(rotas);
    }
}
