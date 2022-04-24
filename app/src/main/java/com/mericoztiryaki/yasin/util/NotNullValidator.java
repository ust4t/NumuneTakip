package com.mericoztiryaki.yasin.util;

import androidx.annotation.NonNull;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by as on 2019-05-07.
 */
public class NotNullValidator extends METValidator {

    public NotNullValidator() {
        super("Bu alan doldurulmak zorundadır.");
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        return !isEmpty;
    }
}
