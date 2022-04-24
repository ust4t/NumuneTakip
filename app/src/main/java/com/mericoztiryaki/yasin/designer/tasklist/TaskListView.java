package com.mericoztiryaki.yasin.designer.tasklist;

import com.mericoztiryaki.yasin.model.response.TaskStatus;

import java.util.List;

/**
 * Created by as on 2019-05-11.
 */
public interface TaskListView {

    void showLoadingDialog();

    void closeLoadingDialog();

    void dataFetched(List<TaskStatus> taskList);
}
