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
            InthegraService service = new InthegraService("ef5f05bdedd34cada40187761d5daaa7", "erickpassos@gmail.com", "circ51sp");
            AndroidFileHandler fileHandler = new AndroidFileHandler();
            cachedService = new CachedInthegraService(service, fileHandler, 1, TimeUnit.DAYS);

        }
    }

    public static CachedInthegraService getInstance() {
        return cachedService;
    }


}
