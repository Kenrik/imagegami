package com.kenrikmarch.admin.imagegami.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageResult implements Parcelable {

    public String fullURL;
    public String title;
    public String thumbURL;
    public String width;
    public String height;

    public ImageResult(JSONObject json) {
        try {
            this.fullURL  = json.getString("url");
            this.thumbURL = json.getString("tbUrl");
            this.title    = json.getString("title");
            this.height   = json.getString("height");
            this.width    = json.getString("width");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ImageResult> fromJsonArray(JSONArray array) {
        ArrayList <ImageResult> results = new ArrayList<ImageResult>();
       for (int i = 0; i < array.length(); i++) {
           try {
            results.add(new ImageResult(array.getJSONObject(i)));
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }
        return results;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(fullURL);
        out.writeString(title);
        out.writeString(thumbURL);
        out.writeString(width);
        out.writeString(height);
    }

    public static final Parcelable.Creator<ImageResult> CREATOR
            = new Parcelable.Creator<ImageResult>() {
        public ImageResult createFromParcel(Parcel in) {
            return new ImageResult(in);
        }
        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };

    private ImageResult(Parcel in) {
      this.fullURL  = in.readString();
      this.title    = in.readString();
      this.thumbURL = in.readString();
      this.width    = in.readString();
      this.height   = in.readString();
    }
}
