package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import android.content.Context;

import com.equalsp.stransthe.CachedInthegraService;
import com.equalsp.stransthe.InthegraAPI;
import com.equalsp.stransthe.InthegraService;
import com.hcordeiro.andoird.exemplointhegraapi.Util.Util;

import java.util.concurrent.ExecutionException;
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

            new InthegraAsync(context).execute();
        }
    }

    public static CachedInthegraService getInstance() {
        return cachedService;
    }


}
