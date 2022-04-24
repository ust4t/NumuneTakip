package com.mericoztiryaki.yasin.common;

import com.google.gson.JsonElement;
import com.mericoztiryaki.yasin.model.TaskLog;
import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.AppResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by as on 2019-05-16.
 */
public interface CommonService {

    @GET("/api/endpoint/interval.php")
    Call<JsonElement> getInterval();

    @GET("/api/endpoint/task.php?action=detail")
    Call<AppResponse<TaskStatus>> getTaskById(@Query("taskId") int taskId);

    @GET("/api/endpoint/queue.php?action=queue_list")
    Call<AppResponse<List<TaskStatus>>> getTasksOf(@Query("userId") int userId,
                                                          @Query("isToday") int isToday,
                                                          @Query("isCompleted") int isCompleted);

    @GET("/api/endpoint/task.php?action=start_task")
    Call<AppResponse> startTask(@Query("taskId") int taskId);

    @POST("/api/endpoint/task.php?action=complete_task")
    Call<AppResponse> completeTask(@Body TaskLog taskLog);

    @POST("/api/endpoint/task.php?action=finish_task")
    Call<AppResponse> finish_task(@Body TaskLog taskLog);

}
