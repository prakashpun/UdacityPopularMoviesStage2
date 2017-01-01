package com.prakashpun.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by prakash_pun on 12/29/2016.
 */

public class Review implements Parcelable {


    @SerializedName("id")
    private String reviewId;
    @SerializedName("author")
    private String reviewAuthor;
    @SerializedName("content")
    private String reviewContent;
    @SerializedName("url")
    private String reviewUrl;

    public String getReviewContent() {
        return reviewContent;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public static final Parcelable.Creator<Review> CREATOR = new Creator<Review>() {
        public Review createFromParcel(Parcel source) {
            Review review = new Review();
            review.reviewId = source.readString();
            review.reviewAuthor = source.readString();
            review.reviewContent = source.readString();
            review.reviewUrl = source.readString();
            return review;
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(reviewId);
        parcel.writeString(reviewAuthor);
        parcel.writeString(reviewContent);
        parcel.writeString(reviewUrl);
    }
}
