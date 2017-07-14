package lemon.pear.maxim.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import cn.smssdk.SMSSDK;
import lemon.pear.maxim.Config;

/**
 * 初始化操作
 */

public class InitService extends IntentService {

    public InitService() {
        super("InitService");
    }

    private static final String ACTION_INIT = "lemon.pear.maxim.init";

    public static void actionInit(Context context) {
        Intent intent = new Intent(context, InitService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        initMob();
        initLogger();
    }

    /**
     * 日志打印
     */
    private void initLogger() {
        Logger.init(Config.LOG_TAG)
                .methodCount(1)
                .hideThreadInfo()
                .logLevel(Config.DEBUG ? LogLevel.FULL : LogLevel.NONE)
                .methodOffset(0);
    }

    /**
     * 短信验证服务
     */
    private void initMob() {
        SMSSDK.initSDK(this, "1d9874b32895e", "76fcf18a5e9227fe2a9abdc3ecb01bd0");
    }

}
