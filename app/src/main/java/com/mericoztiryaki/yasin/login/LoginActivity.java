package com.mericoztiryaki.yasin.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.mericoztiryaki.yasin.Constants;
import com.mericoztiryaki.yasin.R;
import com.mericoztiryaki.yasin.UserType;
import com.mericoztiryaki.yasin.common.taskdetail.TaskDetailActivity;
import com.mericoztiryaki.yasin.core.arch.BaseActivity;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.designer.tasklist.TaskListActivity;
import com.mericoztiryaki.yasin.model.LocalUser;
import com.mericoztiryaki.yasin.util.LoadingDialog;
import com.mericoztiryaki.yasin.util.persistance.LocalUserHandler;
import com.mericoztiryaki.yasin.util.NotNullValidator;
import com.mericoztiryaki.yasin.util.persistance.LocalWorkHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView, View.OnClickListener {

    private MaterialEditText usernameEditText;
    private MaterialEditText passwordEditText;
    private Button loginButton;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.addValidator(new NotNullValidator());

        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.addValidator(new NotNullValidator());

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        loadingDialog = new LoadingDialog(this, getString(R.string.login_laoding));

        //usernameEditText.setText("urge1");
       // passwordEditText.setText("123x");
    }

    @Override
    public LoginPresenter createPresenter(Context context) {
        return new LoginPresenter(this, new AppTaskExecutor(this), context);
    }

    @Override
    public void onClick(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (usernameEditText.validate() & passwordEditText.validate()) {
      /*      String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();*/

            presenter.login(username, password);
            Log.d("x3k", "username : " + username + ", password : " + password);
        }//#END_IF

    }

    @Override
    public void loginSuccessful() {
    }

    @Override
    public void loginFailed() {
        String title = getString(R.string.login_fail_title);
        String message = getString(R.string.login_fail_message);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void showLoadingDialog() {
        loadingDialog.show();
    }

    @Override
    public void closeLoadingDialog() {
        loadingDialog.dismiss();
    }

    @Override
    public void openNextActivity(LocalUser localUser) {
        // If user type is ADMIN, don't allow to use the app.
        // If user type is TASARIMCI, open designer's task list activity.
        // For other user types, if there is no active task open its task list activity.
        //                       if there is active task, open that task and continue counting.

        Intent intent = null;
        Constants.userType = localUser.getUserType();
        if (localUser.getUserType() == UserType.ADMIN) {
            new LocalUserHandler(this).removeLocalUser();

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.login_fail_admin_title))
                    .setMessage(R.string.login_fail_admin_msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        } else if (localUser.getUserType() == UserType.TASARIMCI) {
            intent = new Intent(LoginActivity.this, TaskListActivity.class);
        } else {
            LocalWorkHandler workHandler = new LocalWorkHandler(this);

            if (workHandler.isLocalWorkExists()) {
                intent = new Intent(LoginActivity.this, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.ARG_TASK_ID_EXTRA, workHandler.getLocalWork().getTaskId());
            } else {
                intent = new Intent(LoginActivity.this, com.mericoztiryaki.yasin.common.tasklist.TaskListActivity.class);
            }
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
