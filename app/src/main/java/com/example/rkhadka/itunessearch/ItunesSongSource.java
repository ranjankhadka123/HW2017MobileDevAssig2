package com.example.rkhadka.itunessearch;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItunesSongSource {
    public static String mquery;
    public interface ItemListener{
        void onSongResponse(List<Songs> songsList);
    }

    private final static int IMAGE_CACHE_COUNT = 100;
    private static ItunesSongSource sItunesSongSourceInstance;

    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static ItunesSongSource get(Context context){
        if(sItunesSongSourceInstance == null) {
            sItunesSongSourceInstance = new ItunesSongSource(context);
        }
        return sItunesSongSourceInstance;
    }
    public static void setQuery(String query){
        mquery = query;
    }
    private ItunesSongSource(Context context){
        mContext = context.getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(context);

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(IMAGE_CACHE_COUNT);
            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url,bitmap);
            }
        });
    }

    public void getSongs(ItemListener songListener){
        final ItemListener songListenerInternal = songListener;
        String url = "https://itunes.apple.com/search?term=" + mquery + "&entity=musicTrack";
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<Songs> songsList = new ArrayList<Songs>();
                    JSONArray songsObj = response.getJSONArray("results");
                    for (int i = 0; i < songsObj.length(); i++) {
                        JSONObject key = songsObj.getJSONObject(i);
                        Songs song = new Songs(key);
                        songsList.add(song);

                    }
                    songListenerInternal.onSongResponse(songsList);
                } catch (JSONException e) {
                    e.printStackTrace();
                    songListenerInternal.onSongResponse(null);
                    Toast.makeText(mContext, "Could not get songs.", Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                songListenerInternal.onSongResponse(null);
                Toast.makeText(mContext, "Could not get songs", Toast.LENGTH_SHORT);
            }
        });
        mRequestQueue.add(jsonObjRequest);


    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}
