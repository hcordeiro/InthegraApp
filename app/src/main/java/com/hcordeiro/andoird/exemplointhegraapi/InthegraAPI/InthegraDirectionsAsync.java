package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hugo on 19/05/16.
 */
public class InthegraDirectionsAsync extends AsyncTask<Trecho, Void, Void> implements DialogInterface.OnCancelListener {
    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";

    private ProgressDialog dialog;
    private AlertDialog alert;
    private Context mContext;
    final private GoogleMap map;

    public InthegraDirectionsAsync(Context context, GoogleMap gMap){
        mContext = context;
        map = gMap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setMessage("Não foi possível criar Rota");
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton("Certo",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertBuilder.create();
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Carregando rota...");
        dialog.show();
    }

    @Override
    protected Void doInBackground(Trecho... params) {
        Trecho trecho = params[0];
        getDirections(trecho.getOrigem(), trecho.getDestino(), MODE_WALKING);
        dialog.dismiss();
        return null;
    }

    public void getDirections(Localizacao origem, Localizacao destino, String mode) {
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
                        Log.d("ReponseOK", "Response OK");
                        try {
                            final JSONObject json = new JSONObject(response);
                            JSONArray routeArray = json.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);
                            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                            String encodedString = overviewPolylines.getString("points");
                            List<LatLng> directions = PolyUtil.decode(encodedString);

                            Polyline line = map.addPolyline(new PolylineOptions()
                                    .addAll(directions)
                                    .width(12)
                                    .color(Color.parseColor("#05b1fb"))
                                    .geodesic(true)
                            );

                        } catch (JSONException e) {
                            alert.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alert.show();
                    }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onCancel(DialogInterface dialog) {cancel(true);}

}
