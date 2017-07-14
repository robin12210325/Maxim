package lemon.pear.maxim.toolkit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import lemon.pear.maxim.module.maxim.activity.DetailActivity;

/**
 * Activity跳转帮助类
 */

public class IntentHelper {

    public static final String OBJECT_ID = "object_id";

    public static final String MAXIM_TITLE = "maxim_title";

    public static final String MAXIM_TYPE = "maxim_type";

    public static void showMaximActivity(Activity activity, String objectId, String title) {
        Intent intent = new Intent(activity, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(OBJECT_ID, objectId);
        bundle.putString(MAXIM_TITLE, title);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, 1024);
    }
}
