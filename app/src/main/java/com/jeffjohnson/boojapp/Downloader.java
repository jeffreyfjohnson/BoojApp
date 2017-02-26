package com.jeffjohnson.boojapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jeffreyjohnson on 2/25/17.
 */

public class Downloader {
    private static final int READ_BUFFER = 2048;
    private static Downloader instance;

    private volatile boolean isDownloadingList = false;

    private Downloader(){}

    public static Downloader getInstance() {
        if (instance == null){
            instance = new Downloader();
        }
        return instance;
    }

    public void getRealtorList(final RealtorListCallback callback){
        if (isDownloadingList){
            return;
        }
        isDownloadingList = true;

        new AsyncTask<Void,Void, List<Realtor>>(){
            @Override
            protected List<Realtor> doInBackground(Void... voids) {
                List<Realtor> realtorList = null;
                try {
                    realtorList = getRealtorList();
                }
                catch (IOException e){
                    Log.e("Realtor List Error", e.getMessage(), e);
                }
                return realtorList;
            }

            @Override
            protected void onPostExecute(List<Realtor> realtorList) {
                super.onPostExecute(realtorList);

                isDownloadingList = false;
                if (realtorList == null){
                    callback.onException();
                }else {
                    callback.onRealtorListDownloaded(realtorList);
                }
            }
        }.execute();
    }

    private List<Realtor> getRealtorList() throws IOException{
        Uri realtorListUri = Uri.parse("http://www.denverrealestate.com")
                .buildUpon()
                .appendEncodedPath("rest.php")
                .appendEncodedPath("mobile")
                .appendEncodedPath("realtor")
                .appendEncodedPath("list")
                .appendQueryParameter("app_key", "f7177163c833dff4b38fc8d2872f1ec6").build();

        String response = getJsonString(realtorListUri.toString());

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Realtor.class, new Realtor.RealtorDeserializer());
        Gson gson = builder.create();

        //use the TypeToken to easily deserialize the realtor list
        return gson.fromJson(response, new TypeToken<List<Realtor>>(){}.getType());
    }

    public byte[] getRawData(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            //the connection is now open and streaming
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException("error while downloading realtor list: "
                        + connection.getResponseMessage());
            }

            int bytes = 0;
            byte[] buffer = new byte[READ_BUFFER];
            //read() will return -1 at the end of the stream
            while((bytes = inputStream.read(buffer)) > 0){
                //write the input to the output stream with the same buffer
                outputStream.write(buffer, 0, bytes);
            }
            outputStream.close();
            return outputStream.toByteArray();
        }
        finally {
            //no matter what happens, make sure we disconnect
            connection.disconnect();
        }
    }

    public String getJsonString(String urlString) throws IOException{
        //heavy lifting done in getRawData, here we just convert the raw data to a string
        return new String(getRawData(urlString));
    }

    interface RealtorListCallback{
        void onRealtorListDownloaded(List<Realtor> realtorList);
        void onException();
    }

}
