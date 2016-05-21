package com.hcordeiro.android.InthegraApp.InthegraAPI;

import android.util.Log;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.InthegraService;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;
import com.equalsp.stransthe.rotas.PontoDeInteresse;
import com.equalsp.stransthe.rotas.Rota;
import com.equalsp.stransthe.rotas.RotaService;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Singleton para manter uma instância do CachedInthegraService sempre carregada.
 *
 * Created by hugo on 17/05/16.
 */
public class InthegraServiceSingleton {
    private final String TAG = "ServiceSingleton";
    private static CachedInthegraService cachedService;
    private static RotaService rotaService;

    public static void initInstance() {
        Log.i(TAG, "initInstance Called");
        if (cachedService == null) {
            InthegraService service = new InthegraService("aa91935448534d519da1cda34d0b1ee4", "c2387331@trbvn.com", "c2387331@trbvn.com");
            AndroidFileHandler fileHandler = new AndroidFileHandler();
            cachedService = new CachedInthegraService(service, fileHandler, 7, TimeUnit.DAYS);
            rotaService = new RotaService(cachedService);
        }
    }

    public static CachedInthegraService getInstance() {
        Log.i(TAG, "getInstance Called");
        return cachedService;
    }

    public static List<Parada> getParadas() throws IOException {
        return getParadas(null);
    }

    public static List<Parada> getParadas(Linha linha) throws IOException {
        Log.i(TAG, "getParadas Called");
        List<Parada> paradas;
        if (linha == null) {
            paradas = cachedService.getParadas();
        } else {
            paradas = cachedService.getParadas(linha);
        }

        if(!paradas.isEmpty()) {
            Collections.sort(paradas, new Comparator<Parada>() {
                @Override
                public int compare(Parada p1, Parada p2) {
                    return p1.getCodigoParada().compareTo(p2.getCodigoParada());
                }
            });
        }
        return paradas;
    }

    public static List<Linha> getLinhas() throws IOException {
       return getLinhas(null);
    }

    public static List<Linha> getLinhas(Parada parada) throws IOException {
        Log.i(TAG, "getLinhas Called");
        List<Linha> linhas;

        if(parada == null) {
            linhas = cachedService.getLinhas();
        } else {
            linhas = cachedService.getLinhas(parada);
        }

        if (!linhas.isEmpty()) {
            Collections.sort(linhas, new Comparator<Linha>() {
                @Override
                public int compare(Linha l1, Linha l2) {
                    return l1.getCodigoLinha().compareTo(l2.getCodigoLinha());
                }
            });
        }
        return linhas;
    }

    public static List<Veiculo> getVeiculos(Linha linha) throws IOException {
        Log.i(TAG, "getVeiculos Called");
        List<Veiculo> veiculos;
        if(linha == null) {
            veiculos = cachedService.getVeiculos();
        } else {
            veiculos = cachedService.getVeiculos(linha);
        }

        if (!veiculos.isEmpty()) {
            Collections.sort(veiculos, new Comparator<Veiculo>() {
                @Override
                public int compare(Veiculo v1, Veiculo v2) {
                    return v1.getCodigoVeiculo().compareTo(v2.getCodigoVeiculo());
                }
            });
        }
        return veiculos;
    }

    public static Set<Rota> getRotas(LatLng origem, LatLng destino, double distanciaMaxima) throws IOException {
        Log.i(TAG, "getRotas Called");
        assert origem != null;
        double origemLat = origem.latitude;
        double origemLng = origem.longitude;

        assert destino != null;
        double destinoLat = destino.latitude;
        double destinoLng = destino.longitude;

        PontoDeInteresse p1 = new PontoDeInteresse(origemLat, origemLng);
        PontoDeInteresse p2 = new PontoDeInteresse(destinoLat, destinoLng);

        return rotaService.getRotas(p1, p2, distanciaMaxima);
    }
}
