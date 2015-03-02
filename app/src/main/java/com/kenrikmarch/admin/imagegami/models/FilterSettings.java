package com.kenrikmarch.admin.imagegami.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 3/1/15.
 */
public class FilterSettings implements Parcelable {

    public String size  = "none";
    public String color = "none";
    public String type  = "none";
    public String site = "";

    public FilterSettings() {
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(size);
        out.writeString(color);
        out.writeString(type);
        out.writeString(site);
    }

    public static final Parcelable.Creator<FilterSettings> CREATOR
            = new Parcelable.Creator<FilterSettings>() {
        public FilterSettings createFromParcel(Parcel in) {
            return new FilterSettings(in);
        }
        public FilterSettings[] newArray(int size) {
            return new FilterSettings[size];
        }
    };

    public FilterSettings(Parcel in) {
        this.size  = in.readString();
        this.color = in.readString();
        this.type  = in.readString();
        this.site  = in.readString();
    }
}
