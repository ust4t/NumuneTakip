package com.mericoztiryaki.yasin.designer.tasklist;

import com.mericoztiryaki.yasin.core.network.RetrofitClientInstance;
import com.mericoztiryaki.yasin.designer.TaskService;
import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.AppResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by as on 2019-05-11.
 */
public class TaskListDataSource {

    public static List<TaskStatus> getTaskListForCreator(int creatorId, boolean isToday, boolean isCompleted) {
        TaskService service = RetrofitClientInstance.getRetrofitInstance().create(TaskService.class);

        try {
            AppResponse<List<TaskStatus>> response = service.getTasksCreatedBy(creatorId, isToday ? 1 : 0, isCompleted ? 1 : 0).execute().body();

            if (response != null) {
                if (response.getCode() == 200) {
                    return response.getData();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

}
