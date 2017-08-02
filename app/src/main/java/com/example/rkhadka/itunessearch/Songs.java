package com.example.rkhadka.itunessearch;

import org.json.JSONException;
import org.json.JSONObject;

public class Songs {

    protected String mTrackName;
    protected String mArtistName;
    protected String mAlbumName;
    protected String mURLString;
    protected String mImageURLString;
    protected String mTrackViewUrl;

    public Songs(JSONObject songObj) {
        try {
            mTrackName = songObj.getString("trackName");
            mArtistName = songObj.getString("artistName");
            mAlbumName = songObj.getString("collectionName");
            mURLString = songObj.getString("previewUrl");
            mTrackViewUrl = songObj.getString("trackViewUrl");
            mImageURLString = songObj.getString("artworkUrl60");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public String getmArtistName() {
        return mArtistName;
    }

    public String getmTrackName() {
        return mTrackName;
    }

    public String getmURLString() {
        return mURLString;
    }
    public String getImageURLString() {
        return mImageURLString;
    }

    public String getmTrackViewUrl() {
        return mTrackViewUrl;
    }
}
