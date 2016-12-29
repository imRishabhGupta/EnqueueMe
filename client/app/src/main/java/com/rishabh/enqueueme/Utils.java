package com.rishabh.enqueueme;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.ViewTreeObserver;

/**
 * Created by rohanagarwal94 on 29/12/16.
 */
public class Utils {
    private Utils() {
    }

    public static String getUserId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    }