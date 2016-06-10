package com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Veiculo;
import com.hcordeiro.android.InthegraApp.InthegraAPI.InthegraService;
import com.hcordeiro.android.InthegraApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask resposnável por carregar as informações de véiculos de uma dada linha.
 *
 * Created by hugo on 17/05/16.
 */
public class InthegraVeiculosAsync extends AsyncTask<Linha, Void, List<Veiculo>> implements DialogInterface.OnCancelListener {
    private final String TAG = "FileHandler";
    public InthegraVeiculosAsyncResponse delegate = null;
    private ProgressDialog dialog;
//    private AlertDialog alert;
    private Context mContext;
    private boolean wasUnsuccessful;

    public InthegraVeiculosAsync(Context context){
        Log.d(TAG, "Constructor Called");
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute Called");
        super.onPreExecute();
//        alert = criarAlerta();

        dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getString(R.string.carregando));
        dialog.show();
    }

    private AlertDialog criarAlerta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setMessage(mContext.getString(R.string.erro_carregar_veiculos));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton(mContext.getString(R.string.certo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return alertBuilder.create();
    }

    @Override
    protected List<Veiculo> doInBackground(Linha... params) {
        Log.d(TAG, "doInBackground Called");
        Linha linha = params[0];
        List<Veiculo> veiculos = new ArrayList<>();
        try {
            Log.d(TAG, "Recuperando veículos da linha... ");
            veiculos = InthegraService.getVeiculos(linha);
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível carregar os veículos da linha, motivo: " + e.getMessage());
            wasUnsuccessful = true;
        } finally {
            dialog.dismiss();
        }
        return veiculos;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel Called");
        cancel(true);
    }

    @Override
    protected void onPostExecute(List<Veiculo> veiculos) {
        Log.d(TAG, "onPostExecute Called");
        if (wasUnsuccessful) {
            Toast.makeText(mContext, mContext.getString(R.string.erro_carregar_veiculos), Toast.LENGTH_SHORT).show();
        }
        delegate.processFinish(veiculos);
    }
}
