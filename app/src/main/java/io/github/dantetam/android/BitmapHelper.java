package io.github.dantetam.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dante on 7/19/2016.
 */
public class BitmapHelper {

    private static LruCache<String, Bitmap> mMemoryCache;
    private static Context context;

    private static Map<Integer, String> namesById;

    public static void init(Context c) {
        context = c;
        namesById = new HashMap<>();
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 6;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    private static void addBitmap(String key, Bitmap bitmap) {
        if (getBitmap(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private static Bitmap getBitmap(String key) {
        return mMemoryCache.get(key);
    }

    public static Bitmap findBitmapOrBuild(int resourceId) {
        if (resourceId == 0) {
            return null;
        }
        String name;
        if (namesById.containsKey(resourceId)) {
            name = namesById.get(resourceId);
        }
        else {
            name = context.getResources().getResourceEntryName(resourceId);
            namesById.put(resourceId, name);
        }
        Bitmap bitmapStored = getBitmap(name);
        if (bitmapStored != null) {
            return bitmapStored;
        }
        else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;	// No pre-scaling
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            mMemoryCache.put(name, bitmap);
            return bitmap;
        }

    }

}
