package com.mericoztiryaki.yasin.core.task;

/**
 * Created by as on 2019-05-07.
 */
public interface AppTask<T> {

    T execute();

    void onPostExecute(T result);

    abstract class SimpleAppTask implements AppTask<Boolean> {

        @Override
        public Boolean execute(){
            simpleExecute();
            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            onPostExecute();
        }

        public abstract void simpleExecute();

        public abstract void onPostExecute();
    }
}
