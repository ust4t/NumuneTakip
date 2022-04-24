package com.mericoztiryaki.yasin.util.persistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mericoztiryaki.yasin.UserType;
import com.mericoztiryaki.yasin.model.LocalUser;

/**
 * Created by as on 2019-05-08.
 */
public class LocalUserHandler {

    private static final String TAG_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_TYPE = "user_type";

    private SharedPreferences preferences;

    private static LocalUser localUser;

    public LocalUserHandler(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isLocalUserExists() {
        return getLocalUser() != null;
    }

    public LocalUser getLocalUser() {
        if (localUser == null) {
            int id = preferences.getInt(TAG_ID, -1);
            String username = preferences.getString(TAG_USERNAME,null);
            String userType = preferences.getString(TAG_TYPE, null);

            if (id != -1 && username != null && userType != null) {
                localUser = new LocalUser(id, username, UserType.valueOf(userType));
            }
        }
        return localUser;
    }


    public LocalUser saveLocalUser(int id, String username, UserType userType) {
        removeLocalUser();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TAG_ID, id);
        editor.putString(TAG_USERNAME, username);
        editor.putString(TAG_TYPE, userType.toString());
        editor.commit();

        localUser = new LocalUser(id, username, userType);
        return localUser;
    }

    public void removeLocalUser() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(TAG_ID, -1);
        editor.putString(TAG_USERNAME, null);
        editor.putString(TAG_TYPE, null);
        editor.commit();

        localUser = null;
    }

}
