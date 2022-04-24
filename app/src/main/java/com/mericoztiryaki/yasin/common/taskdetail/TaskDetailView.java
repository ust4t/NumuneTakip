package com.mericoztiryaki.yasin.common.taskdetail;

import com.mericoztiryaki.yasin.model.response.TaskStatus;

/**
 * Created by as on 2019-05-16.
 */
public interface TaskDetailView {

    void showLoadingDialog();

    void closeLoadingDialog();

    void onTaskFetched(TaskStatus task);

    void onIntervalFetched(int interval);

    void onTaskEnded();
}
