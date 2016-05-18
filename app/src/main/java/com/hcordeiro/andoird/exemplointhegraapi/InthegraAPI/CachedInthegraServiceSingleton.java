package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import android.content.Context;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.InthegraService;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.equalsp.stransthe.Veiculo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by hugo on 17/05/16.
 */
public class CachedInthegraServiceSingleton {
    private static CachedInthegraService cachedService;

    public static void initInstance(Context context) {
        if (cachedService == null) {
            InthegraService service = new InthegraService("aa91935448534d519da1cda34d0b1ee4", "c2387331@trbvn.com", "c2387331@trbvn.com");
            AndroidFileHandler fileHandler = new AndroidFileHandler();
            cachedService = new CachedInthegraService(service, fileHandler, 7, TimeUnit.DAYS);

            new InthegraCacheAsync(context).execute();
        }
    }

    public static CachedInthegraService getInstance() {
        return cachedService;
    }

    public static List<Parada> getParadas() throws IOException {
        return getParadas(null);
    }

    public static List<Parada> getParadas(Linha linha) throws IOException {
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
        List<Linha> linhas = new ArrayList<>();

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
        List<Veiculo> veiculos = new ArrayList<>();
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
}
