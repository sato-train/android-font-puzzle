package com.sato_lab.game.unlock;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sato on 2015/09/11.
 */
public class CsvUtil {
    static public List<String> loadFile(Context csContext, String strCsvFileName) {
        InputStream csInputStream = null;
        BufferedReader csBufReader = null;
        List<String> sCsvList = new ArrayList();

        try {
            AssetManager csAsset = csContext.getResources().getAssets();
            csInputStream = csAsset.open(strCsvFileName);
            csBufReader = new BufferedReader(new InputStreamReader(csInputStream));

            String line = csBufReader.readLine();

            System.out.println(line);

            if (line != null) {
                for (String token : line.split(",")) {
                    sCsvList.add(token);
                }
            }

            csBufReader.close();

        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

        return sCsvList;
    }
}