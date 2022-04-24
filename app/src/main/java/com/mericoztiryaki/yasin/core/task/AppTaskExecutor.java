package com.mericoztiryaki.yasin.core.task;

/**
 * Created by as on 2019-05-07.
 */

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by as on 6.02.2018.
 */

public class AppTaskExecutor implements TaskExecutor {

    private final WeakReference<Activity> mActivityWeakReference;

    public AppTaskExecutor(Activity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public <T> void async(AppTask<T> task) {
        new SimpleAsyncTask<>(task).execute();
    }

    private class SimpleAsyncTask<R> extends AsyncTask<Void, Void, R> {

        private final AppTask<R> mTask;

        public SimpleAsyncTask(final AppTask<R> task) {
            mTask = task;
        }

        @Override
        protected R doInBackground(final Void... params) {
            return mTask.execute();
        }

        @Override
        protected void onPostExecute(final R r) {
            if (activityNotFinished()) {
                mTask.onPostExecute(r);
            }
        }

        private boolean activityNotFinished() {
            final Activity activity = mActivityWeakReference.get();
            return activity != null && !activity.isFinishing();
        }
    }
}
