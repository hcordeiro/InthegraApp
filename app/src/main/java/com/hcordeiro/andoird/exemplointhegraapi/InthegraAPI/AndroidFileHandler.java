package com.hcordeiro.andoird.exemplointhegraapi.InthegraAPI;

import android.content.Context;
import android.os.Environment;

import com.equalsp.stransthe.CachedServiceFileHander;
import com.equalsp.stransthe.Linha;
import com.equalsp.stransthe.Parada;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hugo on 17/05/16.
 */
public class AndroidFileHandler implements CachedServiceFileHander {

    @Override
    public String loadCacheFile() throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"cachedInthegraService.json");
        String fileContent = "";

        if (file.exists()) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            br.close();

            fileContent = stringBuilder.toString();
        }
        return fileContent;
    }

    @Override
    public void saveCacheFile(String content) throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"cachedInthegraService.json");

        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.flush();
        writer.close();
    }
}
