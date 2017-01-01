package com.prakashpun.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prakash_pun on 12/29/2016.
 */

public class Reviews {

    @SerializedName("results")
    private List<Review> reviews = new ArrayList<>();

    public List<Review> getReviews() {
        return reviews;
    }
}
