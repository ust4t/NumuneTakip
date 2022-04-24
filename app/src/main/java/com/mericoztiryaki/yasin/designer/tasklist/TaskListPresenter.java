package com.mericoztiryaki.yasin.designer.tasklist;

import com.mericoztiryaki.yasin.core.arch.BasePresenter;
import com.mericoztiryaki.yasin.core.task.AppTask;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.model.response.TaskStatus;

import java.util.List;

/**
 * Created by as on 2019-05-11.
 */
public class TaskListPresenter extends BasePresenter {

    private TaskListView taskListView;
    private AppTaskExecutor executor;

    public TaskListPresenter(TaskListView taskListView, AppTaskExecutor executor) {
        this.taskListView = taskListView;
        this.executor = executor;
    }

    public void fetchData(int creatorId, boolean isToday, boolean isCompleted) {
        executor.async(new TaskListFetcher(creatorId, isToday, isCompleted));
    }

    private class TaskListFetcher implements AppTask<List<TaskStatus>> {

        private int creatorId;
        private boolean isToday;
        private boolean isCompleted;

        public TaskListFetcher(int creatorId, boolean isToday, boolean isCompleted) {
            this.creatorId = creatorId;
            this.isToday = isToday;
            this.isCompleted = isCompleted;

            taskListView.showLoadingDialog();
        }

        @Override
        public List<TaskStatus> execute() {
            return TaskListDataSource.getTaskListForCreator(creatorId, isToday, isCompleted);
        }

        @Override
        public void onPostExecute(List<TaskStatus> result) {
            taskListView.dataFetched(result);
            taskListView.closeLoadingDialog();
        }
    }
}
