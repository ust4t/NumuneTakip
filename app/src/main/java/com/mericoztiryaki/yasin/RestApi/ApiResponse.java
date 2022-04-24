package com.mericoztiryaki.yasin.RestApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mericoztiryaki.yasin.model.Task;
import com.mericoztiryaki.yasin.model.UserPojo;

public class ApiResponse {


    @SerializedName("id")
    private int id = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SerializedName("error")
    private boolean error = true;

    @SerializedName("message")
    private String message = "";

    @SerializedName("errorMessage")
    public String errorMessage = "";

    @SerializedName("pendingTasks")
    private Task[] pendingTasks;

    @SerializedName("modelists")
    private UserPojo[] modelists;

    public UserPojo[] getModelists() {
        return modelists;
    }

    public void setModelists(UserPojo[] modelists) {
        this.modelists = modelists;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Task[] getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(Task[] pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
}
