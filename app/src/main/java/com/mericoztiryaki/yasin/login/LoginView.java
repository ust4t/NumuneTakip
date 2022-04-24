package com.mericoztiryaki.yasin.login;

import com.mericoztiryaki.yasin.model.LocalUser;

/**
 * Created by as on 2019-05-07.
 */
public interface LoginView {

    void loginSuccessful();

    void loginFailed();

    void showLoadingDialog();

    void closeLoadingDialog();

    void openNextActivity(LocalUser localUser);

}
