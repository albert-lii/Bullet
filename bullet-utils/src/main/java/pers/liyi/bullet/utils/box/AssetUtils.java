package pers.liyi.bullet.utils.box;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;


public class AssetUtils {

    public static String readJsonFile(@NonNull Context context, @NonNull String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bf = new BufferedReader(isr);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
