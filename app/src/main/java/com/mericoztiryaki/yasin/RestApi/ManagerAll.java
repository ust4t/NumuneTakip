package com.mericoztiryaki.yasin.RestApi;

import com.google.gson.JsonObject;
import com.mericoztiryaki.yasin.ROS;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;

public class ManagerAll extends BaseManager {


    private static ManagerAll ourInstance = new ManagerAll();

    public static synchronized ManagerAll getInstance() {
        return ourInstance;
    }

    //Singleton Design Pattern
    private ManagerAll() {
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "read error";
        }
    }

    public Call<ResponseBody> uploadImages(RequestBody jsonObject, List<MultipartBody.Part> images) {
        Call<ResponseBody> x = getRestApiClient().uploadImages(jsonObject, images);

        ROS.log("Body : " + bodyToString(jsonObject));
        ;
        return x;
    }

    public Call<ResponseBody> getPendingTask(int userID) {
        Call<ResponseBody> x = getRestApiClient().getPendingTask(userID);
        return x;
    }

    public Call<ResponseBody> setTaskResult(int taskID, int result,String note) {
        Call<ResponseBody> x = getRestApiClient().setTaskResult(taskID, result,note);
        return x;
    }

    public Call<ResponseBody> getModelists() {
        Call<ResponseBody> x = getRestApiClient().getModelists();
        return x;
    }

    public Call<ResponseBody> getProductIntervals(int taskID) {
        Call<ResponseBody> x = getRestApiClient().getProductIntervals(taskID);
        return x;
    }

    public Call<ResponseBody> updateTaskDate(int taskID,String date) {
        Call<ResponseBody> x = getRestApiClient().updateTaskDate(taskID,date);
        return x;
    }

    public Call<ResponseBody> createTask() {
        Call<ResponseBody> x = getRestApiClient().getModelists();
        return x;
    }
}





















