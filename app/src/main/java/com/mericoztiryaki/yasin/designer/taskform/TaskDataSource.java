package com.mericoztiryaki.yasin.designer.taskform;

import com.google.gson.Gson;
import com.mericoztiryaki.yasin.ROS;
import com.mericoztiryaki.yasin.core.network.RetrofitClientInstance;
import com.mericoztiryaki.yasin.designer.TaskService;
import com.mericoztiryaki.yasin.model.ProductType;
import com.mericoztiryaki.yasin.model.Task;
import com.mericoztiryaki.yasin.model.response.TaskResponse;
import com.mericoztiryaki.yasin.util.AppResponse;
import com.mericoztiryaki.yasin.util.ImageFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by as on 2019-05-09.
 */
public class TaskDataSource {

    public static List<ProductType> fetchAllProductTypes() {
        TaskService service = RetrofitClientInstance.getRetrofitInstance().create(TaskService.class);

        try {
            Response<List<ProductType>> response = service.getAllProductTypes().execute();
            ROS.log("fetchAllProductTypes1 : " + response.toString() + " :  " + response.errorBody() + " :  " + response.isSuccessful());
            if (response.code() == 200) {
                return response.body();
            }
        } catch (IOException e) {
            ROS.loge("fetchAllProductTypes : ", e);
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static TaskResponse createTask(Task task) {
        TaskService service = RetrofitClientInstance.getRetrofitInstance().create(TaskService.class);

        try {
            String strJson = new Gson().toJson(task);
            ROS.log("**CREATE TASK **"+strJson);
            AppResponse<TaskResponse> response = service.createTask(task).execute().body();

            if (response.getCode() == 200) {
                return response.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean updateTask(int id, Task task) {
        TaskService service = RetrofitClientInstance.getRetrofitInstance().create(TaskService.class);

        try {
            AppResponse<TaskResponse> response = service.updateTask(id, task).execute().body();

            if (response.getCode() == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean getPendingTasks(int id) {
        TaskService service = RetrofitClientInstance.getRetrofitInstance().create(TaskService.class);

        try {
            AppResponse<TaskResponse> response = service.getPendingTask(id).execute().body();

            if (response.getCode() == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean sendImage(ImageFile imageFile) {
        TaskService service = RetrofitClientInstance.getRetrofitInstance().create(TaskService.class);

        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile.getBytes());
            MultipartBody.Part body = MultipartBody.Part.createFormData("imageFileName", imageFile.getFileName(), requestFile);
            AppResponse response = service.uploadImage(body).execute().body();

            if (response.getCode() == 200) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
