package com.jeffjohnson.boojapp;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class ListPictureDownloadService extends IntentService {

    public static final String EXTRA_PIC_POSITION = "com.jeffjohnson.pic_position";
    public static final String INTENT_LIST_PIC = "com.jeffjohnson.detail_pic";

    public ListPictureDownloadService() {
        super("ListPictureDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getDataString();
        PictureCache cache = PictureCache.getInstance();
        if (cache.get(url) == null){
            try {
                //download the image from the URL
                byte[] bitmapArray = Downloader.getInstance().getRawData(url);
                //put it in the cache
                cache.put(url, BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length));
            }
            catch (IOException e){
                Log.e("ListPictureDownload", "error downloading picture", e);
            }
        }

        int position = intent.getIntExtra(EXTRA_PIC_POSITION, -1);
        Intent broadcastDownload = new Intent(INTENT_LIST_PIC);
        broadcastDownload.putExtra(EXTRA_PIC_POSITION, position);
        //broadcast to all listening parties that a new image has been downloaded. The position in
        //the list is passed along with the intent
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastDownload);
    }
}
