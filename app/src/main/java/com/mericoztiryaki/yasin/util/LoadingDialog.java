package com.mericoztiryaki.yasin.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by as on 2019-05-07.
 */
public class LoadingDialog extends ProgressDialog {


    public LoadingDialog(Context context, String message) {
        super(context);
        this.setMessage(message);
    }

}
