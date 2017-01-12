package com.jerry.authoritativeguide.util;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jerry.authoritativeguide.modle.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 2017/1/12.
 */

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "44cf4fb5a2f5d8d16e9524fbc8644525";


    /**
     * 根据URL地址返回一个字节流数组
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            int length;
            byte[] buffer = new byte[1024];

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 根据URL地址返回一个字符串
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 获取内容
     */
    public List<GalleryItem> fetchItems(int page) {
        List<GalleryItem> galleryItems = new ArrayList<>();


        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", String.valueOf(page))
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);

            JSONObject jsonObject = new JSONObject(jsonString);
            galleryItems = parseItems(jsonObject);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to fetch items", e);
            e.printStackTrace();
        }

        return galleryItems;
    }

    /**
     * 将GalleryItem数据集合解析成List返回
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    private List<GalleryItem> parseItems(JSONObject jsonObject) throws JSONException {

        JSONObject photosJsonObject = jsonObject.getJSONObject("photos");

        JSONArray photosJasonArray = photosJsonObject.getJSONArray("photo");

        Log.i(TAG, "所需要的集合Json: " + photosJasonArray.toString());

        return new Gson().fromJson(photosJasonArray.toString(), new TypeToken<List<GalleryItem>>() {
        }.getType());
    }


}
