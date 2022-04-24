package com.mericoztiryaki.yasin.common.tasklist;

import com.mericoztiryaki.yasin.common.CommonService;
import com.mericoztiryaki.yasin.core.network.RetrofitClientInstance;

import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.AppResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by as on 2019-05-11.
 */
public class TaskListDataSource {

    public static List<TaskStatus> getTasksFromQueueOfLocalUser(int userId, boolean isToday, boolean isCompleted) {
        CommonService service = RetrofitClientInstance.getRetrofitInstance().create(CommonService.class);

        try {
            AppResponse<List<TaskStatus>> response = service.getTasksOf(userId, isToday ? 1:0, isCompleted ? 1:0).execute().body();

            if(response!=null){
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
