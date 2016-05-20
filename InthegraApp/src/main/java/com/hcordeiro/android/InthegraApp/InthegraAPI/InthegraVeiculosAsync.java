package com.hcordeiro.android.InthegraApp.InthegraAPI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Veiculo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo on 17/05/16.
 */
public class InthegraVeiculosAsync extends AsyncTask<Linha, Void, List<Veiculo>> implements DialogInterface.OnCancelListener {
    public InthegraVeiculosAsyncResponse delegate = null;
    private ProgressDialog dialog;
    private AlertDialog alert;
    private Context mContext;
    private boolean wasUnsuccessful;

    public InthegraVeiculosAsync(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setMessage("Não foi possível recuperar os veículos da Linha informada");
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton("Certo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertBuilder.create();

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Carregando veículos...");
        dialog.show();
    }

    @Override
    protected List<Veiculo> doInBackground(Linha... params) {
        Linha linha = params[0];
        List<Veiculo> veiculos = new ArrayList<>();
        try {
            veiculos = InthegraServiceSingleton.getVeiculos(linha);
        } catch (IOException e) {
            wasUnsuccessful = true;
        } finally {
            dialog.dismiss();
        }
        return veiculos;
    }

    @Override
    public void onCancel(DialogInterface dialog) {cancel(true);}

    @Override
    protected void onPostExecute(List<Veiculo> veiculos) {
        if (wasUnsuccessful) {
            alert.show();
        }
        delegate.processFinish(veiculos);
    }
}
