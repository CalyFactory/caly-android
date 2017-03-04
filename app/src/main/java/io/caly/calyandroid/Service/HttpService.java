package io.caly.calyandroid.Service;

import io.caly.calyandroid.Model.Response.AccountResponse;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.Response.EventResponse;
import io.caly.calyandroid.Model.Response.RecoResponse;
import io.caly.calyandroid.Model.Response.SessionResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project Caly
 * @since 17. 2. 11
 */

public interface HttpService {

    @GET("/users/{user}/repos")
    Call<BasicResponse> test(
            @Path("user") String user
    );


    /*
    =============================
                MEMBER
    =============================
     */

    //loginCheck
    @FormUrlEncoded
    @POST("member/loginCheck")
    Call<SessionResponse> loginCheck(
            @Field("uId") String userId,
            @Field("uPw") String userPw,
            @Field("uuid") String uuid,
            @Field("apikey") String apiKey,
            @Field("loginPlatform") String loginPlatform,
            @Field("subject") String subject,
            @Field("appVersion") String appVersion/*,
            @Field("sdkLevel") String sdkLevel*/
    );

    //signUp
    @FormUrlEncoded
    @POST("member/signUp")
    Call<SessionResponse> signUp(
            @Field("uId") String userId,
            @Field("uPw") String userPw,
            @Field("authCode") String authCode,
            @Field("gender") int gender,
            @Field("birth") int birth,
            @Field("loginPlatform") String loginPlatform,
            @Field("pushToken") String pushToken,
            @Field("deviceType") int deviceType,
            @Field("appVersion") String appVersion,
            @Field("deviceInfo") String deviceInfo,
            @Field("uuid") String uuid,
            @Field("sdkLevel") String sdkLevel
    );

    //registerDevice
    @FormUrlEncoded
    @POST("member/registerDevice")
    Call<SessionResponse> registerDevice(
            @Field("apikey") String apiKey,
            @Field("pushToken") String pushToken,
            @Field("deviceType") int deviceType,
            @Field("appVersion") String appVersion,
            @Field("deviceInfo") String deviceInfo,
            @Field("uuid") String uuid,
            @Field("sdkLevel") String sdkLevel
    );

    //logout
    @FormUrlEncoded
    @POST("member/logout")
    Call<BasicResponse> logout(
            @Field("apikey") String apiKey
    );

    //checkVersion
    @FormUrlEncoded
    @POST("member/checkVersion")
    Call<BasicResponse> checkVersion(
            @Field("appVersion") String appVersion,
            @Field("apikey") String apiKey
    );

    //accountList
    @FormUrlEncoded
    @POST("member/accountList")
    Call<AccountResponse> accountList(
            @Field("apikey") String apikey
    );

    //addAccount
    @FormUrlEncoded
    @POST("member/addAccount")
    Call<BasicResponse> addAccount(
            @Field("apikey") String apikey,
            @Field("login_platform") String loginPlatform,
            @Field("uId") String userId,
            @Field("uPw") String userPw,
            @Field("authCode") String authCode
    );

    /*
    =============================
                EVENT
    =============================
     */

    @FormUrlEncoded
    @POST("events/getList")
    Call<EventResponse> getEventList(
            @Field("apikey") String apiKey,
            @Field("pageNum") int pageNum
    );



    /*
    =============================
                SYNC
    =============================
     */

    //sync
    @FormUrlEncoded
    @POST("sync")
    Call<BasicResponse> sync(
            @Field("apikey") String apiKey
    );


    /*
    =============================
                RECO
    =============================
     */

    //getList
    @FormUrlEncoded
    @POST("reco/getList")
    Call<RecoResponse> getRecoList(
            @Field("apikey") String apiKey,
            @Field("eventHashkey") String eventHashKey,
            @Field("category") String category
    );

    //tracking
    @FormUrlEncoded
    @POST("reco/tracking")
    Call<BasicResponse> tracking(
            @Field("apikey") String apiKey,
            @Field("eventHashkey") String eventHashkey,
            @Field("recoHashkey") String recoHashkey,
            @Field("type") String type
    );


    /*
    =============================
              SETTING
    =============================
     */

    //updatePushToken
    @FormUrlEncoded
    @POST("setting/updatePushToken")
    Call<BasicResponse> updatePushToken(
            @Field("pushToken") String pushToken,
            @Field("apikey") String apiKey
    );

    //setReceivePush
    @FormUrlEncoded
    @POST("setting/setReceivePush")
    Call<BasicResponse> setReceivePush(
            @Field("apikey") String apiKey,
            @Field("receive") int receive
    );



    /*
    =============================
              SUPPORT
    =============================
     */

    //notices
    @FormUrlEncoded
    @POST("support/notices")
    Call<BasicResponse> notices(
            @Field("apikey") String apiKey
    );



}
