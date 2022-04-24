package com.mericoztiryaki.yasin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mericoztiryaki.yasin.RestApi.ApiResponse;
import com.mericoztiryaki.yasin.RestApi.ManagerAll;
import com.mericoztiryaki.yasin.model.Task;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class actConfirmTask extends AppCompatActivity {
    ArrayList<Task> arrayList = new ArrayList<>();
    Activity act;
    RecyclerView rcyPendingTask;

    final PendingTaskAdapter.RecyclerViewClickListener listener = new PendingTaskAdapter.RecyclerViewClickListener() {
        @Override
        public void onClick(Object o, int position) {
            final Task task = ((Task) o);


        }//End OnClick

    };//End RecyclerViewClickListener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_confirm_task);
        act = actConfirmTask.this;

        rcyPendingTask = findViewById(R.id.rcyPendingTask);

        Call<ResponseBody> callPendingTask = ManagerAll.getInstance().getPendingTask(2);

        callPendingTask.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String strJson = ROS.printResponse("callPendingTask : ", response);
                    ApiResponse apiResponse = new Gson().fromJson(strJson, ApiResponse.class);
                    Task[] arrTask = apiResponse.getPendingTasks();

                    ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(arrTask));
                    PendingTaskAdapter pendingTaskAdapter = new PendingTaskAdapter(act, taskList, listener);
                    rcyPendingTask.setHasFixedSize(true);
                    rcyPendingTask.setLayoutManager(new LinearLayoutManager(act));
                    rcyPendingTask.setAdapter(pendingTaskAdapter);
                } catch (Exception e) {
                    ROS.loge("Error Pending Task : ", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
}
