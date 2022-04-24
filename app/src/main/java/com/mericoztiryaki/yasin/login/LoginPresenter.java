package com.mericoztiryaki.yasin.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mericoztiryaki.yasin.core.arch.BasePresenter;
import com.mericoztiryaki.yasin.core.task.AppTask;
import com.mericoztiryaki.yasin.core.task.TaskExecutor;
import com.mericoztiryaki.yasin.model.LocalUser;
import com.mericoztiryaki.yasin.model.response.LoginResponse;
import com.mericoztiryaki.yasin.util.persistance.LocalUserHandler;

/**
 * Created by as on 2019-05-07.
 */
public class LoginPresenter extends BasePresenter {

    private LoginView loginView;
    private TaskExecutor taskExecutor;
    private LocalUserHandler localUserHandler;

    public LoginPresenter(LoginView view, TaskExecutor taskExecutor, Context context) {
        this.loginView = view;
        this.taskExecutor = taskExecutor;
        this.localUserHandler = new LocalUserHandler(context);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (localUserHandler.isLocalUserExists()) {
            loginView.openNextActivity(localUserHandler.getLocalUser());
        }
    }

    public void login(String username, String password) {
        loginView.showLoadingDialog();

        taskExecutor.async(new LoginTask(username, password));
    }

    private class LoginTask implements AppTask<LoginResponse> {

        private String username;
        private String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public LoginResponse execute() {
            return LoginDataSource.findUserByUsernameAndPassword(username, password);
        }

        @Override
        public void onPostExecute(LoginResponse result) {
            loginView.closeLoadingDialog();

            if (result != null) {
                loginView.loginSuccessful();

                LocalUser localUser =
                        localUserHandler.saveLocalUser(result.getId(), result.getUsername(), result.getUserType());

                loginView.openNextActivity(localUser);
            }
            else {

               // Log.d("x3k","loginFailed : "+result.getUsername()+" : "+result.getId()+" : "+result.getUserType()+" : "+result.toString());
                Log.d("x3k","loginFailed :");
                loginView.loginFailed();
            }
        }
    }
}
