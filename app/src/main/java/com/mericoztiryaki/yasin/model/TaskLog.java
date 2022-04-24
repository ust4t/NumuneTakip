package com.mericoztiryaki.yasin.model;

/**
 * Created by as on 2019-05-17.
 */
public class TaskLog {

    private int userId;
    private int taskId;
    private int normalTime;
    private int normalTimeElapsed;
    private int extraTime;
    private int extraTimeElapsed;

    public TaskLog(int userId, int taskId, int normalTime, int normalTimeElapsed, int extraTime, int extraTimeElapsed) {
        this.userId = userId;
        this.taskId = taskId;
        this.normalTime = normalTime;
        this.normalTimeElapsed = normalTimeElapsed;
        this.extraTime = extraTime;
        this.extraTimeElapsed = extraTimeElapsed;
    }

    public TaskLog(int userId, int taskId, int normalTime, int normalTimeElapsed) {
        this.userId = userId;
        this.taskId = taskId;
        this.normalTime = normalTime;
        this.normalTimeElapsed = normalTimeElapsed;
        this.extraTime = -1;
        this.extraTimeElapsed = -1;
    }
}
