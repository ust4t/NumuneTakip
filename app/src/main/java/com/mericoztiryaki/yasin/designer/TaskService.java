package com.mericoztiryaki.yasin.designer;

import com.mericoztiryaki.yasin.model.ProductType;
import com.mericoztiryaki.yasin.model.Task;
import com.mericoztiryaki.yasin.model.response.TaskResponse;
import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.AppResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by as on 2019-05-09.
 */
public interface TaskService {

    @POST("/api/endpoint/product.php")
    Call<List<ProductType>> getAllProductTypes();

    @POST("/api/endpoint/task.php?action=create")
    Call<AppResponse<TaskResponse>> createTask(@Body Task task);

    @POST("/api/endpoint/task.php?action=update")
    Call<AppResponse> updateTask(@Query("id") int id, @Body Task task);

    @Multipart
    @POST("/api/endpoint/image.php")
    Call<AppResponse> uploadImage(@Part MultipartBody.Part image);

    @GET("/api/endpoint/queue.php?action=creator_list")
    Call<AppResponse<List<TaskStatus>>> getTasksCreatedBy(@Query("creatorId") int creatorId,
                                                          @Query("isToday") int isToday,
                                                          @Query("isCompleted") int isCompleted);

    @POST("/api/endpoint/task.php?action=finish_task")
    Call<AppResponse> getPendingTask(@Query("userId") int id);
}
