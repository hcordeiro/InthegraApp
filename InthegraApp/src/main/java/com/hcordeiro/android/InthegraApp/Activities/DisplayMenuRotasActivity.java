package com.hcordeiro.android.InthegraApp.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.hcordeiro.android.InthegraApp.R;

public class DisplayMenuRotasActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "MenuRotas";

    private Button gerarRotaBtn;
    private Location localUsuario;
    private LatLng origem;
    private LatLng destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate Called");
        super.onCreate(savedInstanceState);
        requestLocation();
        setContentView(R.layout.activity_display_menu_rotas);
        gerarRotaBtn = (Button) findViewById(R.id.gerarRotaBtn);
        assert gerarRotaBtn != null;
        gerarRotaBtn.setEnabled(false);

        final Button selectionarOrigemBtn = (Button) findViewById(R.id.selectionarOrigemBtn);
        assert selectionarOrigemBtn != null;

        Switch meuLocalSwtich = (Switch) findViewById(R.id.meuLocalSwtich);
        assert meuLocalSwtich != null;
        meuLocalSwtich.setChecked(false);
        meuLocalSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectionarOrigemBtn.setEnabled(false);
                    origem = new LatLng(localUsuario.getLatitude(), localUsuario.getLongitude());
                } else {
                    selectionarOrigemBtn.setEnabled(true);
                }
            }
        });


        if (origem != null && destino != null) {
            gerarRotaBtn.setEnabled(true);
        }
    }

    public void selecionarOrigemActivity(View view) {
        Log.i(TAG, "selecionarOrigemActivity Called");
        Intent intent = new Intent(new Intent(this, SelecionarOrigemActivity.class));
        Bundle bundle = new Bundle();
        bundle.putParcelable("Origem", origem);
        intent.putExtra("Bundle", bundle);
        startActivityForResult(intent, 1);
    }

    public void selecionarDestinoActivity(View view) {
        Log.i(TAG, "selecionarDestinoActivity Called");
        Intent intent = new Intent(new Intent(this, SelecionarDestinoActivity.class));
        Bundle bundle = new Bundle();
        bundle.putParcelable("Destino", destino);
        intent.putExtra("Bundle", bundle);
        startActivityForResult(intent, 2);
    }

    public void gerarRotaActivity(View view) {
        Log.i(TAG, "gerarRotaActivity Called");
        Intent intent = new Intent(this, GerarRotaActivity.class);
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
            Bundle bundle = data.getParcelableExtra("Bundle");
            origem = bundle.getParcelable("Origem");
        }

        if (requestCode == 2) {
            Bundle bundle = data.getParcelableExtra("Bundle");
            destino = bundle.getParcelable("Destino");
        }

        if (origem != null && destino != null) {
            gerarRotaBtn.setEnabled(true);
        }
    }

    private void requestLocation() {
        Log.i(TAG, "requestLocation Called");
        final int LOCATION_REFRESH_TIME = 1000;
        final int LOCATION_REFRESH_DISTANCE = 5;
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_ACCESS_LOCATION = 1;
            String[] PERMISSIONS_LOCATION = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            ActivityCompat.requestPermissions(DisplayMenuRotasActivity.this, PERMISSIONS_LOCATION, REQUEST_ACCESS_LOCATION);
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            Log.d(TAG, "Nova localização: " + location.getLatitude() + "," + location.getLongitude());
            localUsuario = location;
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected Called");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended Called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed Called");
    }
}