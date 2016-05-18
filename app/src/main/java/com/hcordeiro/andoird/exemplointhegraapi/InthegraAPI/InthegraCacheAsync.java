package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.equalsp.stransthe.CachedInthegraService;

import java.io.IOException;

/**
 * Created by hugo on 17/05/16.
 */
public class InthegraCacheAsync extends AsyncTask<Void, Void, Void> implements DialogInterface.OnCancelListener {
    private ProgressDialog dialog;
    private Context mContext;

    public InthegraCacheAsync(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        this.dialog.setMessage("Carregando cache... Isso pode demorar alguns minutos");
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        CachedInthegraService cachedService = CachedInthegraServiceSingleton.getInstance();

        try {
            cachedService.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            dialog.dismiss();
        }
        return null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        cancel(true);
    }
}
