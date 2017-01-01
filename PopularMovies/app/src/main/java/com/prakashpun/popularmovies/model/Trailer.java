package com.prakashpun.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by prakash_pun on 12/29/2016.
 */

public class Trailer implements Parcelable {


    @SerializedName("id")
    private String trailerId;
    @SerializedName("key")
    private String trailerKey;
    @SerializedName("name")
    private String trailerName;
    @SerializedName("site")
    private String trailerSite;
    @SerializedName("size")
    private String trailerSize;

    private Trailer(){

    }

    public String getName() {
        return trailerName;
    }

    public String getKey() {
        return trailerKey;
    }

    public String getTrailerUrl() {
        return "http://www.youtube.com/watch?v=" + trailerKey;
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {
        public Trailer createFromParcel(Parcel source) {
            Trailer trailer = new Trailer();
            trailer.trailerId = source.readString();
            trailer.trailerKey = source.readString();
            trailer.trailerName = source.readString();
            trailer.trailerSite = source.readString();
            trailer.trailerSize = source.readString();
            return trailer;
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(trailerId);
        parcel.writeString(trailerKey);
        parcel.writeString(trailerName);
        parcel.writeString(trailerSite);
        parcel.writeString(trailerSize);
    }

}
