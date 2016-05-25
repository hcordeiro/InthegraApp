package com.hcordeiro.android.InthegraApp.Activities.Rotas;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;
import com.hcordeiro.android.InthegraApp.Activities.MenuPrincipalActivity;
import com.hcordeiro.android.InthegraApp.R;
import com.hcordeiro.android.InthegraApp.Util.Util;

public class RotasMenuActivity extends AppCompatActivity {
    private final String TAG = "MenuRotas";

    private Button gerarRotaBtn;
    private Button selectionarOrigemBtn;
    private Location localUsuario;
    private LatLng origem;
    private LatLng destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rotas_menu_activity);
        Util.requestLocation(this, mLocationListener);

        preencherDados();
    }

    private void preencherDados() {
        Log.i(TAG, "preencherDados Called");
        gerarRotaBtn = (Button) findViewById(R.id.gerarRotaBtn);
        assert gerarRotaBtn != null;
        gerarRotaBtn.setEnabled(false);

        selectionarOrigemBtn = (Button) findViewById(R.id.selectionarOrigemBtn);
        assert selectionarOrigemBtn != null;

        Switch meuLocalSwtich = (Switch) findViewById(R.id.meuLocalSwtich);
        assert meuLocalSwtich != null;
        meuLocalSwtich.setChecked(false);

        if (Util.IS_LOCATION_AUTHORIZED) {
            meuLocalSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        origem = null;
                        selectionarOrigemBtn.setEnabled(true);
                        CheckedTextView origemCheckedTextView = (CheckedTextView) findViewById(R.id.origemCheckedTextView);
                        assert origemCheckedTextView != null;
                        origemCheckedTextView.setChecked(false);
                    }
                }
            });
        } else {
            meuLocalSwtich.setEnabled(false);
        }


        if (origem != null && destino != null) {
            gerarRotaBtn.setEnabled(true);
        }
    }

    public void selecionarOrigemActivity(View view) {
        Log.i(TAG, "selecionarOrigemActivity Called");
        Intent intent = new Intent(new Intent(this, RotasSelecionarOrigemActivity.class));
        Bundle bundle = new Bundle();
        bundle.putParcelable("Origem", origem);
        intent.putExtra("Bundle", bundle);
        startActivityForResult(intent, 1);
    }

    public void selecionarDestinoActivity(View view) {
        Log.i(TAG, "selecionarDestinoActivity Called");
        Intent intent = new Intent(new Intent(this, RotasSelecionarDestinoActivity.class));
        Bundle bundle = new Bundle();
        bundle.putParcelable("Destino", destino);
        intent.putExtra("Bundle", bundle);
        startActivityForResult(intent, 2);
    }

    public void gerarRotaActivity(View view) {
        Log.i(TAG, "gerarRotaActivity Called");
        Intent intent = new Intent(this, RotaGerarActivity.class);
        Bundle bundle = new Bundle();

        bundle.putParcelable("Origem", origem);
        bundle.putParcelable("Destino", destino);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult Called");
        if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getParcelableExtra("Bundle");
                if (bundle != null) {
                    origem = bundle.getParcelable("Origem");

                    CheckedTextView origemCheckedTextView = (CheckedTextView) findViewById(R.id.origemCheckedTextView);
                    assert origemCheckedTextView != null;
                    origemCheckedTextView.setChecked(true);

                }
            }
        }

        if (requestCode == 2) {
            if (data != null) {
                Bundle bundle = data.getParcelableExtra("Bundle");
                if (bundle != null) {
                    destino = bundle.getParcelable("Destino");
                    CheckedTextView destinoCheckedTextView = (CheckedTextView) findViewById(R.id.destinoCheckedTextView);
                    assert destinoCheckedTextView != null;
                    destinoCheckedTextView.setChecked(true);
                }
            }
        }

        if (origem != null && destino != null) {
            gerarRotaBtn.setEnabled(true);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            localUsuario = location;

            Switch meuLocalSwtich = (Switch) findViewById(R.id.meuLocalSwtich);
            assert meuLocalSwtich != null;
            if (meuLocalSwtich.isChecked()) {
                selectionarOrigemBtn.setEnabled(false);
                origem = new LatLng(localUsuario.getLatitude(), localUsuario.getLongitude());
                CheckedTextView origemCheckedTextView = (CheckedTextView) findViewById(R.id.origemCheckedTextView);
                assert origemCheckedTextView != null;
                origemCheckedTextView.setChecked(true);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled");
        }
    };

}