package io.caly.calyandroid.Model;

import retrofit2.Call;
import retrofit2.http.Field;
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
    Call<BasicResponse> listRepos(
            @Path("user") String user
    );


    //Member
    @POST("/v1.0/member/loginCheck")
    Call<BasicResponse> loginCheck(
            @Field("uId") String userId,
            @Field("uPw") String userPw,
            @Field("uuid") String uuid,
            @Field("sessionkey") String sessionKey,
            @Field("loginPlatform") String loginPlatform,
            @Field("subject") String subject
    );

}
