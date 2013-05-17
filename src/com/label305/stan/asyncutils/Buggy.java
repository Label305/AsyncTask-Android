package com.label305.stan.asyncutils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import com.label305.stan.utils.HttpHelper;
import com.label305.stan.utils.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Buggy {

    private static final String PLATFORM = "Android";
    private static final String URL = "http://buggy.label305.com/dump";
    private static final String PARAM_PLATFORM = "platform";
    private static final String PARAM_PACKAGE = "package";
    private static final String PARAM_VERSION = "version";
    private static final String PARAM_ERRORTYPE = "error_type";
    private static final String PARAM_LABEL = "label";
    private static final String PARAM_PLATFORMVERSION = "os_version";

    public static void report(final Context context, final Exception exception, final String label) {
        if (context == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> data = new ArrayList<NameValuePair>();

                data.add(new BasicNameValuePair(PARAM_PLATFORM, PLATFORM));
                data.add(new BasicNameValuePair(PARAM_PACKAGE, context.getPackageName()));
                data.add(new BasicNameValuePair(PARAM_VERSION, getVersionName(context)));
                data.add(new BasicNameValuePair(PARAM_ERRORTYPE, exception.getClass().getName()));
                data.add(new BasicNameValuePair(PARAM_LABEL, label));
                data.add(new BasicNameValuePair(PARAM_PLATFORMVERSION, Build.VERSION.RELEASE));

                try {
                    new HttpHelper().post(URL, new HashMap<String, String>(), data);
                } catch (IOException e) {
                    Logger.log(context, e);
                }
            }
        }).start();
    }

    private static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.log(context, e);
            return "?";
        }
    }

}
