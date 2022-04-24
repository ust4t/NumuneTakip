package com.mericoztiryaki.yasin.model;

/**
 * Created by as on 2019-05-16.
 */
public class LocalWork {

    private int userId;
    private int taskId;

    private int normalTime;
    private long startingTime;

    private int extraTime;
    private long extraStartingTime;

    public LocalWork(int userId, int taskId, int normalTime, long startingTime) {
        this.userId = userId;
        this.taskId = taskId;
        this.normalTime = normalTime;
        this.startingTime = startingTime;
        this.extraTime = -1;
        this.extraStartingTime = -1;
    }

    public LocalWork(int userId, int taskId, int normalTime, long startingTime, int extraTime, long extraStartingTime) {
        this.userId = userId;
        this.taskId = taskId;
        this.normalTime = normalTime;
        this.startingTime = startingTime;
        this.extraTime = extraTime;
        this.extraStartingTime = extraStartingTime;
    }

    public void setExtraTime(int extraTime, long extraStartingTime) {
        this.extraTime = extraTime;
        this.extraStartingTime = extraStartingTime;
    }

    public boolean hasExtraTime() {
        return this.extraTime != -1 && this.extraStartingTime != -1;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getUserId() {
        return userId;
    }

    public long getStartingTime() {
        return startingTime;
    }

    public int getExtraTime() {
        return extraTime;
    }

    public long getExtraStartingTime() {
        return extraStartingTime;
    }

    public int getNormalTime() {
        return normalTime;
    }
}
