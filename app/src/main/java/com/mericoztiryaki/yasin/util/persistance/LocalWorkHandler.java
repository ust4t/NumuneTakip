package com.mericoztiryaki.yasin.util.persistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mericoztiryaki.yasin.model.LocalWork;

/**
 * Created by as on 2019-05-16.
 */
public class LocalWorkHandler {

    private static final String TAG_USER_ID = "task_user_id";
    private static final String TAG_TASK_ID = "task_id";
    private static final String TAG_NORMAL_TIME = "task_normal_time";
    private static final String TAG_STARTING_TIME = "task_starting_time";
    private static final String TAG_EXTRA_TIME = "task_extra_time";
    private static final String TAG_EXTRA_STARTING_TIME = "task_extra_starting_time";

    private SharedPreferences preferences;

    private static LocalWork localWork;

    public LocalWorkHandler(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLocalWorkExists() {
        return getLocalWork() != null;
    }

    public LocalWork getLocalWork() {
        if (localWork == null) {
            int userId = preferences.getInt(TAG_USER_ID, -1);
            int taskId = preferences.getInt(TAG_TASK_ID, -1);
            int normalTime = preferences.getInt(TAG_NORMAL_TIME, -1);
            long startingTime = preferences.getLong(TAG_STARTING_TIME, -1l);
            int extraTime = preferences.getInt(TAG_EXTRA_TIME, -1);
            long extraStartingTime = preferences.getLong(TAG_EXTRA_STARTING_TIME, -1l);

            if (userId != 1 && taskId != -1 && startingTime != -1) {
                localWork = new LocalWork(userId, taskId, normalTime, startingTime, extraTime, extraStartingTime);
            }
        }
        return localWork;
    }

    public LocalWork saveLocalWork(int userId, int taskId, int normalTime, long startingTime) {
        removeLocalWork();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TAG_USER_ID, userId);
        editor.putInt(TAG_TASK_ID, taskId);
        editor.putInt(TAG_NORMAL_TIME, normalTime);
        editor.putLong(TAG_STARTING_TIME, startingTime);
        editor.commit();

        localWork= new LocalWork(userId, taskId, normalTime, startingTime);
        return localWork;
    }

    public void addExtraTime(int extraTime, long startingTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TAG_EXTRA_TIME, extraTime);
        editor.putLong(TAG_EXTRA_STARTING_TIME, startingTime);
        editor.commit();

        localWork.setExtraTime(extraTime, startingTime);
    }

    public void removeLocalWork() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TAG_USER_ID, -1);
        editor.putInt(TAG_TASK_ID, -1);
        editor.putInt(TAG_NORMAL_TIME,-1);
        editor.putLong(TAG_STARTING_TIME, -1);
        editor.putInt(TAG_EXTRA_TIME, -1);
        editor.putLong(TAG_EXTRA_STARTING_TIME, -1);
        editor.commit();

        localWork = null;
    }

}
