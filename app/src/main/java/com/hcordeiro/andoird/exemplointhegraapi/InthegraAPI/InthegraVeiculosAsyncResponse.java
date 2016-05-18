package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import com.equalsp.stransthe.Veiculo;

import java.util.List;

/**
 * Created by hugo on 17/05/16.
 */
public interface InthegraVeiculosAsyncResponse {
    void processFinish(List<Veiculo> veiculos);
}
