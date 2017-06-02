package com.example.currentplacedetailsonmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dante on 6/2/2017.
 */
public class BitmapManager {

    private static Map<String, Bitmap> bitmapsByString = new HashMap<>();

    public static Bitmap getBitmapFromName(String name, Context context, int id) {
        Bitmap stored = bitmapsByString.get(name);
        if (stored == null) {
            stored = BitmapFactory.decodeResource(context.getResources(), id);
            bitmapsByString.put(name, stored);
        }
        return stored;
    }

}
