package com.mericoztiryaki.yasin.core.task;

/**
 * Created by as on 2019-05-07.
 */
public interface TaskExecutor {

    <T> void async(AppTask<T> task);
}