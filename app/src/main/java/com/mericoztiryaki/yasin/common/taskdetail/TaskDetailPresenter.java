package com.mericoztiryaki.yasin.common.taskdetail;

import android.util.Log;

import com.mericoztiryaki.yasin.core.arch.BasePresenter;
import com.mericoztiryaki.yasin.core.task.AppTask;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.model.LocalWork;
import com.mericoztiryaki.yasin.model.TaskLog;
import com.mericoztiryaki.yasin.model.response.TaskStatus;

/**
 * Created by as on 2019-05-16.
 */
public class TaskDetailPresenter extends BasePresenter {

    private TaskDetailView taskDetailView;
    private AppTaskExecutor taskExecutor;

    public TaskDetailPresenter(TaskDetailView taskDetailView, AppTaskExecutor taskExecutor) {
        this.taskDetailView = taskDetailView;
        this.taskExecutor = taskExecutor;
    }

    public void fetchTask(int taskId) {
        taskExecutor.async(new TaskDetailFetcher(taskId));
    }

    public void fetchInterval(int taskID) {
        taskExecutor.async(new IntervalFetcher(taskID));
    }

    public void startTask(int taskId) {
        taskExecutor.async(new TaskStarterTask(taskId));
    }

    public void endTask(int userId, int taskId, int elapsedTime, LocalWork localWork) {
        TaskLog taskLog = null;
        Log.d("TaskDetail", "-------------------------------------------------");
        if (localWork.hasExtraTime()) {
            Log.d("TaskDetail", "Task ended with extra time.");
            Log.d("TaskDetail", "Requested time: " + localWork.getExtraTime());
            Log.d("TaskDetail", "Elapsed time  : " + elapsedTime);

            taskLog = new TaskLog(userId, taskId, localWork.getNormalTime(), localWork.getNormalTime(), localWork.getExtraTime(), elapsedTime);
        } else {
            Log.d("TaskDetail", "Task ended without extra time.");
            Log.d("TaskDetail", "Requested time: " + localWork.getNormalTime());
            Log.d("TaskDetail", "Elapsed time  : " + elapsedTime);

            taskLog = new TaskLog(userId, taskId, localWork.getNormalTime(), elapsedTime);
        }

        taskExecutor.async(new TaskCompletionTask(taskLog));
    }

    private class IntervalFetcher implements AppTask<Integer> {
        int taskID = -1;

        public IntervalFetcher(int taskID) {
            taskDetailView.showLoadingDialog();
            this.taskID = taskID;
        }

        @Override
        public Integer execute() {
            return TaskDetailDataSource.getInterval(taskID);
        }

        @Override
        public void onPostExecute(Integer result) {
            taskDetailView.onIntervalFetched(result);
            taskDetailView.closeLoadingDialog();
        }
    }

    private class TaskDetailFetcher implements AppTask<TaskStatus> {

        private int taskId;

        public TaskDetailFetcher(int taskId) {
            this.taskId = taskId;
            taskDetailView.showLoadingDialog();
        }

        @Override
        public TaskStatus execute() {
            return TaskDetailDataSource.getTaskById(taskId);
        }

        @Override
        public void onPostExecute(TaskStatus result) {
            taskDetailView.onTaskFetched(result);
            taskDetailView.closeLoadingDialog();
        }

    }

    private class TaskStarterTask implements AppTask<Boolean> {
        private int taskId;

        public TaskStarterTask(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public Boolean execute() {
            return TaskDetailDataSource.startTask(taskId);
        }

        @Override
        public void onPostExecute(Boolean result) {

        }
    }

    private class TaskCompletionTask implements AppTask<Boolean> {
        private TaskLog taskLog;

        public TaskCompletionTask(TaskLog taskLog) {
            this.taskLog = taskLog;
        }

        @Override
        public Boolean execute() {
            return TaskDetailDataSource.completeTask(taskLog);
        }

        @Override
        public void onPostExecute(Boolean result) {
            taskDetailView.onTaskEnded();
        }
    }

}
