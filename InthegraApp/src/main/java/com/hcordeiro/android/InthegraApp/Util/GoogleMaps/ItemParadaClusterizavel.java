package com.hcordeiro.android.InthegraApp.Util.GoogleMaps;

import com.equalsp.stransthe.Parada;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by hugo on 30/05/16.
 */
public class ItemParadaClusterizavel implements ClusterItem {
    private final LatLng posicaoParada;

    public ItemParadaClusterizavel(Parada parada) {
        posicaoParada = new LatLng(parada.getLat(), parada.getLong());
    }

    @Override
    public LatLng getPosition() {
       return posicaoParada;
    }
}
