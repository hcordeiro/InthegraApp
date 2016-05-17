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
    public boolean loadCacheFromFile(Map<Linha, List<Parada>> cacheLinhaParadas, Map<Parada, List<Linha>> cacheParadaLinhas) throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"cachedInthegraService.json");

        if (file.exists()) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            br.close();

            String fileContent = stringBuilder.toString();

            Gson gson = new GsonBuilder().create();
            JsonObject cacheJson = gson.fromJson(fileContent, JsonObject.class);
            long expire = cacheJson.get("expireAt").getAsLong();

            if (System.currentTimeMillis() > expire) {
                return false;
            } else {
                cacheLinhaParadas.clear();
                cacheParadaLinhas.clear();

                JsonArray linhasParadas = cacheJson.getAsJsonArray("linhasParadas");
                for (int i = 0; i < linhasParadas.size(); ++i) {
                    JsonObject jsonObject = linhasParadas.get(i).getAsJsonObject();
                    Linha linha = gson.fromJson(jsonObject.get("linha").getAsString(), Linha.class);
                    JsonArray paradasJsonArray = jsonObject.getAsJsonArray("paradas");

                    List<Parada> paradasDaLinha = new ArrayList<>();
                    for (int j = 0; j < paradasJsonArray.size(); j++) {
                        JsonElement paradaObejct = paradasJsonArray.get(j);
                        Parada parada = gson.fromJson(paradaObejct.getAsString(), Parada.class);
                        paradasDaLinha.add(parada);
                    }

                    cacheLinhaParadas.put(linha, paradasDaLinha);
                }

                JsonArray paradasLinhas = cacheJson.getAsJsonArray("paradasLinhas");
                for (int i = 0; i < paradasLinhas.size(); ++i) {
                    JsonObject jsonObject = paradasLinhas.get(i).getAsJsonObject();

                    Parada parada = gson.fromJson(jsonObject.get("parada").getAsString(), Parada.class);
                    JsonArray linhasJsonArray = jsonObject.getAsJsonArray("linhas");

                    List<Linha> linhasDaParada = new ArrayList<>();
                    for (int j = 0; j < linhasJsonArray.size(); j++) {
                        JsonElement paradaObejct = linhasJsonArray.get(j);
                        Linha linha = gson.fromJson(paradaObejct.getAsString(), Linha.class);
                        linhasDaParada.add(linha);
                    }
                    cacheParadaLinhas.put(parada, linhasDaParada);
                }
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public void saveCacheToFile(Long expireAt, Map<Linha, List<Parada>> cacheLinhaParadas, Map<Parada, List<Linha>> cacheParadaLinhas) throws IOException {
        Gson gson = new GsonBuilder().create();
        JsonObject cachedJsonObject = new JsonObject();

        cachedJsonObject.addProperty("expireAt", gson.toJson(expireAt));

        JsonArray linhasParadasJsonArray = new JsonArray();
        for (Linha linha : cacheLinhaParadas.keySet()) {
            JsonObject linhaParadaJsonObject = new JsonObject();
            String linhaJson = gson.toJson(linha);

            List<Parada> paradas = cacheLinhaParadas.get(linha);
            JsonArray paradasJsonArray = new JsonArray();
            for (Parada parada : paradas) {
                String paradaJson = gson.toJson(parada);
                paradasJsonArray.add(paradaJson);
            }
            linhaParadaJsonObject.addProperty("linha", linhaJson);
            linhaParadaJsonObject.add("paradas", paradasJsonArray);

            linhasParadasJsonArray.add(linhaParadaJsonObject);
        }
        cachedJsonObject.add("linhasParadas", linhasParadasJsonArray);

        JsonArray paradasLinhasJsonArray = new JsonArray();
        for (Parada parada : cacheParadaLinhas.keySet()) {
            JsonObject paradaLinhasJsonObject = new JsonObject();
            String paradaJson = gson.toJson(parada);

            List<Linha> linhas = cacheParadaLinhas.get(parada);
            JsonArray linhasJsonArray = new JsonArray();
            for (Linha linha : linhas) {
                String linhaJson = gson.toJson(linha);
                linhasJsonArray.add(linhaJson);
            }
            paradaLinhasJsonObject.addProperty("parada", paradaJson);
            paradaLinhasJsonObject.add("linhas", linhasJsonArray);

            paradasLinhasJsonArray.add(paradaLinhasJsonObject);
        }
        cachedJsonObject.add("paradasLinhas", paradasLinhasJsonArray);

        String cacheJson = gson.toJson(cachedJsonObject);
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"cachedInthegraService.json");

        FileWriter writer = new FileWriter(file);
        writer.write(cacheJson);
        writer.flush();
        writer.close();
    }
}
