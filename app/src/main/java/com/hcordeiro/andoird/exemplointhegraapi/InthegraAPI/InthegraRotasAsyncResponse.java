package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import com.equalsp.stransthe.Veiculo;
import com.equalsp.stransthe.rotas.Rota;

import java.util.List;
import java.util.Set;

/**
 * Created by hugo on 18/05/16.
 */
public interface InthegraRotasAsyncResponse {
    void processFinish(Set<Rota> rotas);
}
