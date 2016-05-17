package com.hcordeiro.andoird.exemplointhegraapi.Util;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI.InthegraAsync;

/**
 * Created by hugo on 17/05/16.
 */
public class PopupDialogue extends DialogFragment {
    public PopupDialogue newInstace() {
        PopupDialogue dialogFragment = new PopupDialogue();
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //view related code..

        //Calculation logic data load in background
        new InthegraAsync().execute();


        return null;
    }


}
