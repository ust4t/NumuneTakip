package com.mericoztiryaki.yasin.RestApi;

import com.google.gson.JsonObject;
import com.mericoztiryaki.yasin.model.Task;
import com.mericoztiryaki.yasin.model.response.TaskResponse;
import com.mericoztiryaki.yasin.util.AppResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RestApi {

    @Multipart
    @POST("/api/endpoint/uploadFile.php")
    Call<ResponseBody> uploadImages( @Part("json") RequestBody apiRequest,@Part List<MultipartBody.Part> images);

    @POST("/api/endpoint/pendingTask.php")
    Call<ResponseBody> getPendingTask(@Query("userId") int id);

    @POST("/api/endpoint/setTaskResult.php")
    Call<ResponseBody> setTaskResult(@Query("taskID") int taskID,@Query("result") int result,@Query("note") String note);


    @POST("/api/endpoint/getModelists.php")
    Call<ResponseBody> getModelists();

    @POST("/api/endpoint/getProductIntervals.php")
    Call<ResponseBody> getProductIntervals(@Query("taskID") int taskID);

    @POST("/api/endpoint/updateTaskDate.php")
    Call<ResponseBody> updateTaskDate(@Query("taskID") int taskID,@Query("date") String date);

    @POST("/api/endpoint/task.php?action=create")
    Call<AppResponse<TaskResponse>> createTask(@Body Task task);
}
