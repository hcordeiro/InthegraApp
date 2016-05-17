package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import android.os.AsyncTask;

import com.equalsp.stransthe.CachedInthegraService;

import java.io.IOException;

/**
 * Created by hugo on 17/05/16.
 */
public class InthegraAsync extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        pDialog = new ProgressDialog(getActivity());
//        pDialog.setMessage("Calculating ...");
//        pDialog.setIndeterminate(false);
//        pDialog.setCancelable(false);
//        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        CachedInthegraService cachedService = InthegraCachedServiceSingleton.getInstance();

        try {
            cachedService.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        pDialog.dismiss();
    }
}
