package com.mericoztiryaki.yasin.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class SimpleAlertDialog {

    public static void show(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

}
