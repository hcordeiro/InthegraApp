package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import android.content.Context;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.InthegraService;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by hugo on 17/05/16.
 */
public class InthegraCachedServiceSingleton {
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

}
