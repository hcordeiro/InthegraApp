package com.hcordeiro.android.InthegraApp.InthegraAPI.AsyncTasks;

import com.equalsp.stransthe.rotas.Rota;

import java.util.Set;

/**
 * Resposta do InthegraRotasAsync
 *
 * Created by hugo on 18/05/16.
 */
public interface InthegraRotasAsyncResponse {
    void processFinish(Set<Rota> rotas);
}
