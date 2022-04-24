package com.mericoztiryaki.yasin.common.taskdetail;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mericoztiryaki.yasin.ROS;
import com.mericoztiryaki.yasin.RestApi.ManagerAll;
import com.mericoztiryaki.yasin.common.CommonService;
import com.mericoztiryaki.yasin.core.network.RetrofitClientInstance;
import com.mericoztiryaki.yasin.model.TaskLog;
import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.AppResponse;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by as on 2019-05-16.
 */
public class TaskDetailDataSource {

    public static int getInterval(int taskID) {
        int time = -1;
/*
        CommonService service = RetrofitClientInstance.getRetrofitInstance().create(CommonService.class);

        try {
            JsonElement response = service.getInterval().execute().body();
//            ROS.log("getInterval : " + response.toString());
            ROS.log("getInterval : " + response.getAsString());
            ROS.log("getInterval : " + response.getAsJsonObject().get("interval").getAsInt());
            return response.getAsJsonObject().get("interval").getAsInt();
        } catch (Exception e) {
            ROS.loge("getInterval Error : ", e);
            e.printStackTrace();
        }
*/
        try {
            ResponseBody responseBody = ManagerAll.getInstance().getProductIntervals(taskID).execute().body();
            String strTime = responseBody.string();
            ROS.log("strTime : " + strTime + " ,TaskID : " + taskID);

            time = Integer.parseInt(strTime);

        } catch (Exception e) {
            time = 2;
            ROS.loge("getInterval Error : ", e);
            e.printStackTrace();
        }

        return time;
    }

    public static TaskStatus getTaskById(int taskId) {
        CommonService service = RetrofitClientInstance.getRetrofitInstance().create(CommonService.class);

        try {
            AppResponse<TaskStatus> response = service.getTaskById(taskId).execute().body();

            if (response.getCode() == 200) {
                return response.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean startTask(int taskId) {
        CommonService service = RetrofitClientInstance.getRetrofitInstance().create(CommonService.class);

        try {
            Response<AppResponse> appResponseResponse = service.startTask(taskId).execute();
            ROS.log("startTask1 : " + appResponseResponse.isSuccessful());
            ROS.log("startTask2 : " + appResponseResponse.toString());
            ROS.log("startTask3 : " + appResponseResponse.code());
            AppResponse<TaskStatus> response = appResponseResponse.body();

            return response.getCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean completeTask(TaskLog taskLog) {
        CommonService service = RetrofitClientInstance.getRetrofitInstance().create(CommonService.class);

        try {
            Response<AppResponse> appResponseResponse = service.completeTask(taskLog).execute();
            ROS.log("completeTask1 : " + appResponseResponse.isSuccessful());
            ROS.log("completeTask2 : " + appResponseResponse.toString());
            ROS.log("completeTask3 : " + appResponseResponse.code());
            AppResponse<TaskStatus> response = appResponseResponse.body();

            String jsonData = new Gson().toJson(taskLog);
            ROS.log("jsonData  : " + jsonData);
            return response.getCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            ROS.loge("completeTask Error  : ", e);

        }

        return false;
    }

}
