package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.InthegraService;
import com.hcordeiro.andoird.exemplointhegraapi.Util.Util;

import java.util.concurrent.TimeUnit;

/**
 * Created by hugo on 17/05/16.
 */
public class InthegraCachedServiceSingleton {
    private static CachedInthegraService cachedService;

    public static void initInstance(){
        if (cachedService == null) {
            InthegraService service = new InthegraService("key", "email", "senha");
            AndroidFileHandler fileHandler = new AndroidFileHandler();
            cachedService = new CachedInthegraService(service, fileHandler, 7, TimeUnit.DAYS);

        }
    }

    public static CachedInthegraService getInstance() {
        return cachedService;
    }

}
