package com.hcordeiro.android.InthegraApp.Util;

import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * Created by hugo on 22/05/16.
 */
public class GoogleDirectionsAsyncTask extends AsyncTask<Void, Void, Boolean> implements DialogInterface.OnCancelListener {

    @Override
    protected Boolean doInBackground(Void... params) {
        return null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }
}
