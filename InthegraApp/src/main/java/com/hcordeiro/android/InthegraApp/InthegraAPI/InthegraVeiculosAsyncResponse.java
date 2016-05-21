package com.hcordeiro.android.InthegraApp.InthegraAPI;

import com.equalsp.stransthe.Veiculo;

import java.util.List;

/**
 * Resposta do InthegraVeicuosAsync
 * Created by hugo on 18/05/16.
 */
public interface InthegraVeiculosAsyncResponse {
    void processFinish(List<Veiculo> veiculos);
}
