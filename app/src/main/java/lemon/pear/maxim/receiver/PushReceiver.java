package lemon.pear.maxim.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import cn.jpush.android.api.JPushInterface;
import lemon.pear.maxim.main.WelcomeActivity;

/**
 * 极光推送的广播接收
 */

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //用户点击打开了通知,打开自定义的Activity
            Logger.d("打开通知");
            Intent it = new Intent(context, WelcomeActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(it);
        }
    }
}