package com.mericoztiryaki.yasin.designer.taskform;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mericoztiryaki.yasin.core.arch.BasePresenter;
import com.mericoztiryaki.yasin.core.task.AppTask;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.model.ProductType;
import com.mericoztiryaki.yasin.model.Task;
import com.mericoztiryaki.yasin.model.response.TaskResponse;
import com.mericoztiryaki.yasin.util.ImageFile;

import java.util.List;

/**
 * Created by as on 2019-05-09.
 */
public class TaskFormPresenter extends BasePresenter {

    private TaskFormView taskFormView;
    private AppTaskExecutor taskExecutor;

    public TaskFormPresenter(TaskFormView taskFormView, AppTaskExecutor taskExecutor) {
        this.taskFormView = taskFormView;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskExecutor.async(new ProductListTask());
    }

    public void createForm(Task task, ImageFile imageFile) {
        taskExecutor.async(new TaskCreateTask(task, imageFile));
    }

    public void updateForm(int id, Task task, ImageFile imageFile) {
        taskExecutor.async(new TaskUpdateTask(id, task, imageFile));
    }

    private class ProductListTask implements AppTask<List<ProductType>> {

        public ProductListTask() {
            taskFormView.showLoadingDialog();
        }

        @Override
        public List<ProductType> execute() {

            return TaskDataSource.fetchAllProductTypes();
        }

        @Override
        public void onPostExecute(List<ProductType> result) {
            taskFormView.closeLoadingDialog();
            taskFormView.setProductList(result);
        }
    }

    private class TaskCreateTask implements AppTask<TaskResponse> {

        private Task task;
        private ImageFile imageFile;

        public TaskCreateTask(Task task, ImageFile imageFile) {
            this.task = task;
            this.imageFile = imageFile;
            taskFormView.showLoadingDialog();
        }

        @Override
        public TaskResponse execute() {
            return TaskDataSource.createTask(task);
        }

        @Override
        public void onPostExecute(TaskResponse result) {
            if (result != null && imageFile != null) {
                taskExecutor.async(new ImageUploadTask(result.getTaskId(), imageFile, result));
            } else if (result != null) {
                taskFormView.closeLoadingDialog();
                taskFormView.creationSuccessful(result);
            } else {
                taskFormView.closeLoadingDialog();
                taskFormView.creationFailed();
            }
        }
    }

    private class TaskUpdateTask implements AppTask<Boolean> {

        private int id;
        private Task task;
        private ImageFile imageFile;

        public TaskUpdateTask(int id, Task task, ImageFile imageFile) {
            this.id = id;
            this.task = task;
            this.imageFile = imageFile;
            taskFormView.showLoadingDialog();
        }

        @Override
        public Boolean execute() {
            return TaskDataSource.updateTask(id, task);
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result && imageFile != null) {
                taskExecutor.async(new ImageUploadTask(id, imageFile, null));
            } else if (result) {
                taskFormView.closeLoadingDialog();
                taskFormView.creationSuccessful(null);
            } else {
                taskFormView.closeLoadingDialog();
                taskFormView.creationFailed();
            }
        }
    }



    private class ImageUploadTask implements AppTask<Boolean> {

        private int taskId;
        private ImageFile imageFile;
        private TaskResponse formResult;

        public ImageUploadTask(int taskId, ImageFile imageFile, TaskResponse formResult) {
            this.taskId = taskId;
            this.imageFile = imageFile;
            this.formResult = formResult;
        }

        @Override
        public Boolean execute() {
            imageFile.setFileName(generateFileNameFor());
            return TaskDataSource.sendImage(imageFile);
        }

        @Override
        public void onPostExecute(Boolean result) {
            taskFormView.closeLoadingDialog();
            taskFormView.creationSuccessful(this.formResult);
        }

        private String generateFileNameFor() {
            return "task_img_" + taskId + ".jpg";
        }
    }
}
