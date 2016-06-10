package com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.equalsp.stransthe.Localizacao;
import com.equalsp.stransthe.rotas.Trecho;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.hcordeiro.android.InthegraApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * AsyncTask responsável por recuperar as direções de um trecho da API Directions do google;
 *
 * Created by hugo on 19/05/16.
 */
public class InthegraDirectionsAsync extends AsyncTask<Trecho, Void, Void> implements DialogInterface.OnCancelListener {
    private final String TAG = "DirectionsAsync";
    public final static String MODE_WALKING = "walking";

    private ProgressDialog dialog;
    private AlertDialog alert;
    private Context mContext;
    final private GoogleMap map;

    public InthegraDirectionsAsync(Context context, GoogleMap gMap){
        Log.d(TAG, "Constructor Called");
        mContext = context;
        map = gMap;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute Called");
        super.onPreExecute();
        alert = criarAlerta();
        dialog = new ProgressDialog(mContext);
        dialog.setMessage(mContext.getString(R.string.carregando));
        dialog.show();
    }

    private AlertDialog criarAlerta() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setMessage(mContext.getString(R.string.erro_carregar_rotas));
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
    protected Void doInBackground(Trecho... params) {
        Log.d(TAG, "doInBackground Called");
        Trecho trecho = params[0];
        getDirections(trecho.getOrigem(), trecho.getDestino(), MODE_WALKING);
        dialog.dismiss();
        return null;
    }

    public void getDirections(Localizacao origem, Localizacao destino, String mode) {
        Log.d(TAG, "getDirections Called");
        double origemLat = origem.getLat();
        double origemLng = origem.getLong();
        double destinoLat = destino.getLat();
        double destinoLng = destino.getLong();

        String url = "http://maps.googleapis.com/maps/api/directions/json?origin="
                +origemLat + "," + origemLng  + "&destination=" + destinoLat + "," + destinoLng
                + "&sensor=false&units=metric&mode=" + mode + "&API_KEY=AIzaSyC59hde9AxYwSzd_0ccclrAZociQr4PcJk";

        RequestQueue queue = Volley.newRequestQueue(mContext);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Response OK");
                        try {
                            final JSONObject json = new JSONObject(response);
                            JSONArray routeArray = json.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);
                            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                            String encodedString = overviewPolylines.getString("points");
                            List<LatLng> directions = PolyUtil.decode(encodedString);

                            map.addPolyline(new PolylineOptions()
                                    .addAll(directions)
                                    .width(12)
                                    .color(Color.parseColor("#05b1fb"))
                                    .geodesic(true)
                            );

                        } catch (JSONException e) {
                            Log.e(TAG, "Não foi possível processar o JSON, motivo: " + e.getMessage());
                            alert.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "Response NOK");
                        alert.show();
                    }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel Called");
        cancel(true);
    }

}
