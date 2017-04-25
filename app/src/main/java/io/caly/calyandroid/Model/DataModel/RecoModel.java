package io.caly.calyandroid.Model.DataModel;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 28
 */

public class RecoModel {

    @SerializedName("title")
    public String title;

    @SerializedName("deep_url")
    public String deepUrl;

    @SerializedName("category")
    public String category;

    @SerializedName("source_url")
    public String sourceUrl;

    @SerializedName("price")
    public int price;

    @SerializedName("reco_hashkey")
    public String recoHashKey;

    @SerializedName("region")
    public String region;

    @SerializedName("tagNames")
    public String tagNames;

    @SerializedName("map_url")
    public String mapUrl;

    @SerializedName("distance")
    public String distance;

    @SerializedName("event_hashkey")
    public String eventHashKey;

    @SerializedName("img_url")
    public String imgUrl;

    @SerializedName("lng")
    public double lng;

    @SerializedName("lat")
    public double lat;



}
