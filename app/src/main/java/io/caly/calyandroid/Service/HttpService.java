package io.caly.calyandroid.Service;

import io.caly.calyandroid.Model.Response.AccountResponse;
import io.caly.calyandroid.Model.Response.BasicResponse;
import io.caly.calyandroid.Model.Response.EventResponse;
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
            @Field("apitoken") String apiToken,
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
            @Field("apitoken") String apiToken,
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
            @Field("apitoken") String apiToken
    );

    //checkVersion
    @FormUrlEncoded
    @POST("member/checkVersion")
    Call<BasicResponse> checkVersion(
            @Field("appVersion") String appVersion,
            @Field("apitoken") String apiToken
    );

    //accountList
    @FormUrlEncoded
    @POST("member/accountList")
    Call<AccountResponse> accountList(
            @Field("apitoken") String apitoken
    );

    //addAccount
    @FormUrlEncoded
    @POST("member/addAccount")
    Call<BasicResponse> addAccount(
            @Field("apitoken") String apitoken,
            @Field("login_platform") String loginPlatform
    );

    /*
    =============================
                EVENT
    =============================
     */

    @FormUrlEncoded
    @POST("events/getList")
    Call<EventResponse> getList(
            @Field("apitoken") String apiToken,
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
            @Field("apitoken") String apitoken
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
            @Field("apitoken") String apiToken
    );

    //setReceivePush
    @FormUrlEncoded
    @POST("setting/setReceivePush")
    Call<BasicResponse> setReceivePush(
            @Field("pushToken") String pushToken,
            @Field("receive") int receive
    );
}
