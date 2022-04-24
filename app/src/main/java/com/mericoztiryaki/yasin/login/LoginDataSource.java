package com.mericoztiryaki.yasin.login;

import com.mericoztiryaki.yasin.core.network.RetrofitClientInstance;
import com.mericoztiryaki.yasin.model.response.LoginResponse;
import com.mericoztiryaki.yasin.util.AppResponse;

import java.io.IOException;

/**
 * Created by as on 2019-05-08.
 */
public class LoginDataSource {

    public static LoginResponse findUserByUsernameAndPassword(String username, String password) {
        LoginService service = RetrofitClientInstance.getRetrofitInstance().create(LoginService.class);

        try {
            AppResponse<LoginResponse> response = service.findUserByUsernameAndId("login", username, password).execute().body();

            if (response.getCode() == 200) {
                return response.getData();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
