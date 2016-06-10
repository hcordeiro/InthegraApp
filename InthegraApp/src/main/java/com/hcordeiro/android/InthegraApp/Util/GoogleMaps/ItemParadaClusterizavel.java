package com.hcordeiro.android.InthegraApp.Util.GoogleMaps;

import com.equalsp.stransthe.Parada;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * CluesterItem de Parada. Serve para permitir o agrupamento de marcadores no google maps
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
