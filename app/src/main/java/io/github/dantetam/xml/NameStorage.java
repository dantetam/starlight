package io.github.dantetam.xml;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/8/2017.
 */
public class NameStorage {

    private List<String> maleNames, femaleNames;
    private List<String> worldNames;

    public NameStorage() {
        maleNames = new ArrayList<>();
        femaleNames = new ArrayList<>();
        worldNames = new ArrayList<>();
        //TODO: V Testing purposes only
        worldNames.add("TestName");
    }

    public void loadNames(AssetManager assetManager, String maleFileName, String femaleFileName, String worldFileName) {
        try {
            InputStream maleInputStream = assetManager.open(maleFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(maleInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                maleNames.add(line);
            }
            reader.close();

            InputStream femaleInputStream = assetManager.open(femaleFileName);
            reader = new BufferedReader(new InputStreamReader(femaleInputStream));
            while ((line = reader.readLine()) != null) {
                femaleNames.add(line);
            }
            reader.close();

            InputStream worldInputStream = assetManager.open(worldFileName);
            reader = new BufferedReader(new InputStreamReader(worldInputStream));
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    worldNames.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String randomPersonName() {
        if (Math.random() < 0.5) {
            return randomMaleName();
        }
        else {
            return randomFemaleName();
        }
    }

    public String randomMaleName() {
        int index = (int) (Math.random() * maleNames.size());
        return maleNames.get(index);
    }

    public String randomFemaleName() {
        int index = (int) (Math.random() * femaleNames.size());
        return femaleNames.get(index);
    }

    public String randomWorldName() {
        int index = (int) (Math.random() * worldNames.size());
        return worldNames.get(index);
    }

}
