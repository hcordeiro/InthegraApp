package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by hugo on 19/05/16.
 */
public interface InthegraDirectionsAsyncResponse {
    void processFinish(List<LatLng> geoPoints);
}
