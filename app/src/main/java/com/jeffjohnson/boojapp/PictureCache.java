package com.jeffjohnson.boojapp;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Wrapper around the LruCache class. Will remove the least recently used iamge
 * Created by jeffreyjohnson on 2/25/17.
 */

public class PictureCache{

    private static PictureCache instance;
    LruCache<String, Bitmap> cache;

    private PictureCache(){
        //set the capacity of the cache to 1/8 of the available memory
        cache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory()/1024/8)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()/1024;
            }
        };
    }
    public static PictureCache getInstance(){
        if (instance == null){
            instance = new PictureCache();
        }
        return instance;
    }

    public void put(String key, Bitmap val){
        cache.put(key,val);
    }

    public Bitmap get(String key){
        return cache.get(key);
    }

}
