package com.mericoztiryaki.yasin.login;

import com.mericoztiryaki.yasin.model.response.LoginResponse;
import com.mericoztiryaki.yasin.util.AppResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by as on 2019-05-08.
 */
public interface LoginService {

    @FormUrlEncoded
    @POST("/api/endpoint/user.php")
    Call<AppResponse<LoginResponse>> findUserByUsernameAndId(@Field("action") String action,
                                                             @Field("username") String username,
                                                             @Field("password") String password);

}
